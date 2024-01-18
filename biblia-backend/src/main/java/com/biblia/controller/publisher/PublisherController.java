package com.biblia.controller.publisher;

import com.biblia.model.publisher.PublisherCreateRequest;
import com.biblia.model.response.ResponseModel;
import com.biblia.security.CurrentUser;
import com.biblia.security.UserPrincipal;
import com.biblia.service.publisher.PublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/publisher")
public class PublisherController {
    @Autowired
    PublisherService publisherService;

    @PostMapping
    ResponseEntity<?> createPublisher(@CurrentUser UserPrincipal currentUser,
                                      @RequestBody PublisherCreateRequest request) {
        log.info("create publisher with request: " + request.toString());
        long start = System.currentTimeMillis();
        ResponseModel model = publisherService.createPublisher(currentUser, request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping
    ResponseEntity<?> getPublisher(@RequestParam(name = "page", required = false) Integer page,
                                   @RequestParam(name = "size", required = false) Integer size,
                                   @RequestParam(name = "name", required = false) String name) {
        log.info("get publishers");
        long start = System.currentTimeMillis();
        ResponseModel model = publisherService.getPublishers(page, size, name);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping("/{publisher_id}")
    ResponseEntity<?> getPublisher(@PathVariable(name = "publisher_id") Integer publisherId) {
        log.info("get publishers");
        long start = System.currentTimeMillis();
        ResponseModel model = publisherService.getPublisher(publisherId);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @PutMapping("/{publisher_id}")
    ResponseEntity<?> updatePublisher(@CurrentUser UserPrincipal currentUser,
                                      @PathVariable(name = "publisher_id") Integer publisherId,
                                      @RequestBody PublisherCreateRequest request) {
        log.info("update publisher with request: " + request.toString());
        long start = System.currentTimeMillis();
        ResponseModel model = publisherService.updatePublisher(currentUser, publisherId, request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }
}
