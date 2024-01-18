package com.biblia.controller.user;

import com.biblia.entity.RefreshToken;
import com.biblia.exception.TokenRefreshException;
import com.biblia.model.response.ResponseModel;
import com.biblia.model.user.TokenRefreshResponse;
import com.biblia.model.user.SignInRequest;
import com.biblia.model.user.TokenRefreshRequest;
import com.biblia.repository.user.UserRepository;
import com.biblia.security.CurrentUser;
import com.biblia.security.UserResponse;
import com.biblia.security.JwtUtils;
import com.biblia.security.UserPrincipal;
import com.biblia.service.token.RefreshTokenService;
import com.biblia.service.user.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest request) {
        log.info("sign in with request: " + request);
        long start = System.currentTimeMillis();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("sign in successfully, time: " + diff);
        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        UserResponse response = new UserResponse();
        response.setToken(jwt);
        response.setRefreshToken(refreshToken);
        response.setUser(user);
        response.setRoles(roles);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromLoginId(user.getLoginId());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }

    @DeleteMapping("/refresh-token")
    public ResponseEntity<?> profileUser(@Valid @RequestBody TokenRefreshRequest request) {
        log.info("delete refresh token");
        long start = System.currentTimeMillis();
        ResponseModel model = refreshTokenService.deleteByToken(request.getRefreshToken());
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }
}
