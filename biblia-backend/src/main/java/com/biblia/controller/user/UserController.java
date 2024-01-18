package com.biblia.controller.user;

import com.biblia.model.response.ResponseModel;
import com.biblia.model.user.NewPasswordRequest;
import com.biblia.model.user.SignInRequest;
import com.biblia.model.user.UserUpdateRequest;
import com.biblia.model.user.UserRequest;
import com.biblia.security.CurrentUser;
import com.biblia.security.UserPrincipal;
import com.biblia.service.user.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@Valid @RequestBody UserRequest request) {
        log.info("signup user");
        long start = System.currentTimeMillis();
        ResponseModel model = userService.signUpUser(request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signinUser(@Valid @RequestBody SignInRequest request) {
        log.info("signin user with request: " + request.toString());
        long start = System.currentTimeMillis();
        ResponseModel model = userService.signInUser(request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam(name = "login_id") String loginId,
                                        @RequestParam(name = "code") String code) {
        log.info("verify user");
        long start = System.currentTimeMillis();
        ResponseModel model = userService.enableUser(loginId, code);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profileUser(@CurrentUser UserPrincipal currentUser) {
        log.info("get user profile");
        long start = System.currentTimeMillis();
        ResponseModel model = userService.getUserProfile(currentUser);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUser(@CurrentUser UserPrincipal currentUser,
                                        @RequestBody UserUpdateRequest request) {
        log.info("Update user profile with request: " + request.toString());
        long start = System.currentTimeMillis();
        ResponseModel model = userService.updateUser(currentUser, request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestParam(name = "login_id") String loginId) {
        log.info("Reset password for user: " + loginId);
        long start = System.currentTimeMillis();
        ResponseModel model = userService.resetPassword(loginId);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> setNewPassword(@Valid @RequestBody NewPasswordRequest request) {
        log.info("Reset password with request: " + request.toString());
        long start = System.currentTimeMillis();
        ResponseModel model = userService.setNewPassword(request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

}
