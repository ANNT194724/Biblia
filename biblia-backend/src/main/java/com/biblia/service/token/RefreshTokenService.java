package com.biblia.service.token;

import com.biblia.entity.RefreshToken;
import com.biblia.exception.TokenRefreshException;
import com.biblia.model.response.BaseModel;
import com.biblia.model.response.ResponseModel;
import com.biblia.repository.token.RefreshTokenRepository;
import com.biblia.repository.user.UserRepository;
import com.biblia.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${biblia.app.jwtRefreshExpirationMs}")
    private long refreshTokenDuration;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Integer userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findByUserIdAndDeleteFlag(userId, Constants.DELETE_FLAG.NOT_DELETED).get());
        refreshToken.setExpiryTime(Instant.now().plusMillis(refreshTokenDuration));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryTime().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public ResponseModel deleteByToken(String token) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            RefreshToken res = refreshTokenRepository.deleteByToken(token);
            message = "Token deleted for user with id " + res.getUser().getUserId();
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
