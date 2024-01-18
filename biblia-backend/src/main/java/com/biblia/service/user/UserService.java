package com.biblia.service.user;

import com.biblia.entity.*;
import com.biblia.exception.TokenRefreshException;
import com.biblia.mapper.PagedResponseMapper;
import com.biblia.model.response.BaseModel;
import com.biblia.model.response.ResponseModel;
import com.biblia.model.user.*;
import com.biblia.repository.token.RefreshTokenRepository;
import com.biblia.repository.user.RoleRepository;
import com.biblia.model.response.PagedResponse;
import com.biblia.repository.user.UserRepository;
import com.biblia.repository.user.UserRoleRepository;
import com.biblia.security.UserPrincipal;
import com.biblia.repository.token.VerificationTokenRepository;
import com.biblia.service.mail.MailService;
import com.biblia.utils.Constants;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.webjars.NotFoundException;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Value("${biblia.app.resetPasswordExpiration}")
    private long passwordTokenDuration;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VerificationTokenRepository tokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MailService mailService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    public ResponseModel getUserList(Integer page, Integer size, String keyword) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            keyword = StringUtils.trimToEmpty(keyword);
            if (page == null || page <= 0)
                page = Constants.PAGINATION.DEFAULT_PAGE;
            if (size == null || size <= 0)
                size = Constants.PAGINATION.DEFAULT_SIZE;
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<User> authors = userRepository.findByLoginIdContainsOrUsernameContainsAndDeleteFlag
                    (keyword, keyword, Constants.DELETE_FLAG.NOT_DELETED, pageable);
            PagedResponse<?> pagedResponse = PagedResponseMapper.mapper(authors);
            message = "Get user list successfully";
            model.setData(pagedResponse);
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            return model;
        } catch (Exception e) {
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
    }
    
    @Transactional
    public ResponseModel signUpUser(UserRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            boolean userExists = userRepository
                    .findByLoginIdAndDeleteFlag(request.getEmail(), Constants.DELETE_FLAG.NOT_DELETED)
                    .isPresent();
            if (userExists) {
                // TODO check of attributes are the same and
                // TODO if email not confirmed send confirmation email.
                throw new IllegalStateException("email already taken");
            }
            User user = new User();
            user.setLoginId(request.getEmail());
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);
            user.setUsername(request.getUsername());
            Role defaultRole = roleRepository.findByRoleCodeAndDeleteFlag(Constants.ROLE_CODE.USER, Constants.DELETE_FLAG.NOT_DELETED);
            user.setRoles(Collections.singletonList(defaultRole));
            user.setRoleCode(defaultRole.getRoleCode());
            user.setStatus(Constants.ACCOUNT_STATUS.INACTIVE);
            user.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
            user.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
            user.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
            userRepository.save(user);
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), user);
            tokenRepository.save(verificationToken);
            mailService.sendVerificationEmail(user.getLoginId(), token, user.getUsername());
            message = "Create user successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(new BaseModel(HttpStatus.OK.value(), message));
            return model;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
     }

    @Transactional
    public ResponseModel enableUser(String loginId, String token) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            if (userRepository.existsByLoginIdAndStatusAndDeleteFlag(loginId, Constants.ACCOUNT_STATUS.ACTIVE, Constants.DELETE_FLAG.NOT_DELETED)) {
                throw new IllegalStateException("Account already activated");
            }

            Optional<User> userOptional = userRepository.findByLoginIdAndDeleteFlag(loginId, Constants.DELETE_FLAG.NOT_DELETED);
            if (userOptional.isEmpty())
                throw new NotFoundException("User not found");
            User user = userOptional.get();
            VerificationToken verificationToken = tokenRepository.findByUser(user);
            if (verificationToken.getToken().equals(token)) {
                user.setStatus(Constants.ACCOUNT_STATUS.ACTIVE);
                tokenRepository.updateConfirmedAt(user.getUserId(), Timestamp.valueOf(LocalDateTime.now()));
            } else {
                message = "invalid token";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.BAD_REQUEST);
                model.setData(new BaseModel(HttpStatus.BAD_REQUEST.value(), message));
                return model;
            }
            userRepository.save(user);
            message = "Verified user successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(new BaseModel(HttpStatus.OK.value(), message));
            return model;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
    }

    public ResponseModel getUserProfile(UserPrincipal currentUser) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            Optional<User> userOptional = userRepository.findByLoginIdAndDeleteFlag(currentUser.getLoginId(), Constants.DELETE_FLAG.NOT_DELETED);
            if (userOptional.isEmpty())
                throw new NotFoundException("User not found");
            User user = userOptional.get();
            message = "Get user profile successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(user);
            return model;
        } catch (Exception e) {
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
    }

    public ResponseModel signInUser(SignInRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            Optional<User> userOptional = userRepository.findByLoginIdAndDeleteFlag(request.getLoginId(), Constants.DELETE_FLAG.NOT_DELETED);
            if (userOptional.isEmpty())
                throw new NotFoundException("User not found with login ID: " + request.getLoginId());
            User user = userOptional.get();
            if (user.getStatus().equals(Constants.ACCOUNT_STATUS.INACTIVE)) {
                message = "Account not activated";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.BAD_REQUEST);
                model.setData(new BaseModel(HttpStatus.BAD_REQUEST.value(), message));
                return model;
            }
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                message = "Welcome " + user.getUsername();
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.OK);
                model.setData(user);
                return model;
            }
            message = "Wrong password";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.BAD_REQUEST);
            model.setData(new BaseModel(HttpStatus.BAD_REQUEST.value(), message));
            return model;
        } catch (Exception e) {
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
    }

    @Transactional
    public ResponseModel updateUser(UserPrincipal currentUser, UserUpdateRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            Optional<User> userOptional = userRepository.findByLoginIdAndDeleteFlag(currentUser.getLoginId(), Constants.DELETE_FLAG.NOT_DELETED);
            if (userOptional.isEmpty())
                throw new NotFoundException("User not found");
            User user = userOptional.get();
            user.setUsername(request.getUsername());
            if (!StringUtils.isBlank(request.getAvatarUrl()))
                user.setAvatarUrl(request.getAvatarUrl());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setBirthday(request.getBirthday());
            user.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
            userRepository.save(user);
            message = "Updated user successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(user);
            return model;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
    }

    @Transactional
    public ResponseModel updateUserForAdmin(UserPrincipal currentUser, AdminUserUpdateRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            Optional<User> userOptional = userRepository.findByUserIdAndDeleteFlag(request.getUserId(), Constants.DELETE_FLAG.NOT_DELETED);
            if (userOptional.isEmpty()) {
                message = "User not found";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.NOT_FOUND);
                model.setData(new BaseModel(HttpStatus.NOT_FOUND.value(), message));
                return model;
            }
            User user = userOptional.get();
            Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
            if (!StringUtils.isBlank(request.getRoleCode()) && !StringUtils.equals(request.getRoleCode(), user.getRoleCode())) {
                Role role = roleRepository.findByRoleCodeAndDeleteFlag
                        (request.getRoleCode(), Constants.DELETE_FLAG.NOT_DELETED);
                if (Objects.isNull(role)) {
                    message = "Invalid role";
                    model.setDescription(message);
                    model.setResponseStatus(HttpStatus.BAD_REQUEST);
                    model.setData(new BaseModel(HttpStatus.BAD_REQUEST.value(), message));
                    return model;
                }
                user.setRoleCode(request.getRoleCode());
                userRoleRepository.deleteByUserId(user.getUserId());
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getUserId());
                userRole.setRoleId(role.getRoleId());
                userRole.setCreatedTime(currentTime);
                userRole.setUpdatedTime(currentTime);
                userRole.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                userRoleRepository.save(userRole);
            }
            user.setStatus(Objects.isNull(request.getStatus()) ? user.getStatus() : request.getStatus());
            user.setDeleteFlag(Objects.isNull(request.getDeleteFlag()) ? user.getDeleteFlag() : request.getDeleteFlag());
            user.setUpdatedTime(currentTime);
            user.setUpdatedUser(currentUser.getUserId());
            userRepository.save(user);
            refreshTokenRepository.deleteByUserId(user.getUserId());
            message = "Updated user successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(user);
            return model;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
    }

    public ResponseModel resetPassword(String loginId) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            Optional<User> userOptional = userRepository.findByLoginIdAndStatusAndDeleteFlag
                    (loginId, Constants.ACCOUNT_STATUS.ACTIVE, Constants.DELETE_FLAG.NOT_DELETED);
            if (userOptional.isEmpty()) {
                message = "User not found";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.NOT_FOUND);
                model.setData(new BaseModel(HttpStatus.NOT_FOUND.value(), message));
                return model;
            }
            User user = userOptional.get();
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setExpiryTime(Instant.now().plusMillis(passwordTokenDuration));
            refreshToken.setToken(RandomStringUtils.randomAlphanumeric(8));
            refreshToken = refreshTokenRepository.save(refreshToken);
            mailService.sendResetPasswordEmail(user.getLoginId(), user.getUsername(), refreshToken.getToken());
            message = "Send email successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(new BaseModel(HttpStatus.OK.value(), message));
            return model;
        } catch (Exception e) {
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
    }

    @Transactional
    public ResponseModel setNewPassword(NewPasswordRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(request.getToken());
            if (tokenOptional.isEmpty()) {
                message = "Invalid token";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.BAD_REQUEST);
                model.setData(new BaseModel(HttpStatus.BAD_REQUEST.value(), message));
                return model;
            }
            RefreshToken refreshToken = tokenOptional.get();
            if (refreshToken.getExpiryTime().compareTo(Instant.now()) < 0) {
                refreshTokenRepository.delete(refreshToken);
                throw new TokenRefreshException(refreshToken.getToken(), "Token was expired. Please make a new signin request");
            }
            User user = refreshToken.getUser();
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
            refreshTokenRepository.deleteByUserId(user.getUserId());
            message = "Verify token successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(new BaseModel(HttpStatus.OK.value(), message));
            return model;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
    }
}
