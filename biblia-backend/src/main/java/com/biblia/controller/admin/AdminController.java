package com.biblia.controller.admin;

import com.biblia.model.response.ResponseModel;
import com.biblia.model.user.AdminUserUpdateRequest;
import com.biblia.security.CurrentUser;
import com.biblia.security.UserPrincipal;
import com.biblia.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UserService userService;

    @PutMapping("/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateUser(@CurrentUser UserPrincipal currentUser,
                                        @RequestBody AdminUserUpdateRequest request) {
        log.info("Update user");
        long start = System.currentTimeMillis();
        ResponseModel model = userService.updateUserForAdmin(currentUser, request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getUsers(@RequestParam(name = "page") Integer page,
                                      @RequestParam(name = "size") Integer size,
                                      @RequestParam(name = "keyword", required = false) String keyword) {
        log.info("Get users list");
        long start = System.currentTimeMillis();
        ResponseModel model = userService.getUserList(page, size, keyword);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }
}
