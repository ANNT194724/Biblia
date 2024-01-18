package com.biblia.controller.author;

import com.biblia.model.author.AuthorRequest;
import com.biblia.model.response.ResponseModel;
import com.biblia.security.CurrentUser;
import com.biblia.security.UserPrincipal;
import com.biblia.service.author.AuthorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/author")
public class AuthorController {

    @Autowired
    AuthorService authorService;

    @GetMapping("/{author_id}")
    ResponseEntity<?> getPublisher(@PathVariable(name = "author_id") Integer authorId) {
        log.info("get author");
        long start = System.currentTimeMillis();
        ResponseModel model = authorService.getAuthor(authorId);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @PostMapping
    ResponseEntity<?> createAuthor(@CurrentUser UserPrincipal currentUser,
                                   @RequestBody AuthorRequest request) {
        log.info("create author");
        long start = System.currentTimeMillis();
        ResponseModel model = authorService.createAuthor(currentUser, request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping
    ResponseEntity<?> getAuthorList(@RequestParam(name = "name", required = false) String name,
                                    @RequestParam(name = "page", required = false) Integer page,
                                    @RequestParam(name = "size", required = false) Integer size) {
        log.info("get authors");
        long start = System.currentTimeMillis();
        ResponseModel model = authorService.getAuthorList(name, page, size);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @PutMapping("/{author_id}")
    ResponseEntity<?> updateAuthor(@CurrentUser UserPrincipal currentUser,
                                   @PathVariable(name = "author_id") Integer authorId,
                                   @RequestBody AuthorRequest request) {
        log.info("update author with request: " + request.toString());
        long start = System.currentTimeMillis();
        ResponseModel model = authorService.updateAuthor(currentUser, authorId, request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }
}
