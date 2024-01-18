package com.biblia.controller.review;

import com.biblia.model.response.ResponseModel;
import com.biblia.model.review.ReviewRequest;
import com.biblia.security.CurrentUser;
import com.biblia.security.UserPrincipal;
import com.biblia.service.review.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/review")
public class ReviewController {
    @Autowired
    ReviewService reviewService;

    @PostMapping
    ResponseEntity<?> createReview(@CurrentUser UserPrincipal currentUser,
                                   @RequestBody ReviewRequest request) {
        log.info("create review with request: " + request.toString());
        long start = System.currentTimeMillis();
        ResponseModel model = reviewService.createReview(currentUser, request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping
    ResponseEntity<?> getBookPage(@RequestParam(name = "page") Integer page,
                                  @RequestParam(name = "size") Integer size,
                                  @RequestParam(name = "book_id", required = false) Long bookId,
                                  @RequestParam(name = "user_id", required = false) Long userId) {
        log.info("get reviews");
        long start = System.currentTimeMillis();
        ResponseModel model = reviewService.getReviews(page, size, bookId, userId);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }
}
