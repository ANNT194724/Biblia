package com.biblia.controller.book;

import com.biblia.model.book.BookCreateRequest;
import com.biblia.model.response.ResponseModel;
import com.biblia.security.CurrentUser;
import com.biblia.security.UserPrincipal;
import com.biblia.service.book.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    BookService bookService;

    @PostMapping
    ResponseEntity<?> createBook(@CurrentUser UserPrincipal currentUser,
                                 @RequestBody BookCreateRequest request) {
        log.info("create book with request: " + request.toString());
        long start = System.currentTimeMillis();
        ResponseModel model = bookService.createBook(currentUser, request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping
    ResponseEntity<?> getBookPage(@RequestParam(name = "page") Integer page,
                                  @RequestParam(name = "size") Integer size,
                                  @RequestParam(name = "keyword", required = false) String keyword,
                                  @RequestParam(name = "author_id", required = false) Integer authorId,
                                  @RequestParam(name = "publisher", required = false) Integer publisher,
                                  @RequestParam(name = "issuing_house", required = false) Integer issuingHouse,
                                  @RequestParam(name = "published_year", required = false) Integer publishedYear,
                                  @RequestParam(name = "sort_by", required = false) String sortBy,
                                  @RequestParam(name = "sort_direction", required = false) Integer sortDirection) {
        log.info("get books");
        long start = System.currentTimeMillis();
        ResponseModel model = bookService
                .getBooks(page, size, keyword, authorId, publisher, issuingHouse, publishedYear, sortBy, sortDirection);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping("/request")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    ResponseEntity<?> getBookAddRequest(@RequestParam(name = "page") Integer page,
                                        @RequestParam(name = "size") Integer size) {
        log.info("get book add request");
        long start = System.currentTimeMillis();
        ResponseModel model = bookService.getBookRequest(page, size);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @PostMapping("/genre")
    ResponseEntity<?> getBooksByGenre(@RequestParam(name = "page") Integer page,
                                      @RequestParam(name = "size") Integer size,
                                      @RequestBody List<Integer> genreIds) {
        log.info("get books by genres: " + genreIds.toString());
        long start = System.currentTimeMillis();
        ResponseModel model = bookService.getBooksByGenre(genreIds, page, size);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping("/{book_id}")
    ResponseEntity<?> getBookDetail(@PathVariable(name = "book_id") Long bookId,
                                    @RequestParam(name = "user_id", required = false) Long userId) {
        log.info("get book detail");
        long start = System.currentTimeMillis();
        ResponseModel model = bookService.getBookDetail(bookId, userId);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @PutMapping("/{book_id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    ResponseEntity<?> updateBook(@CurrentUser UserPrincipal currentUser,
                                 @PathVariable(name = "book_id") Long bookId,
                                 @RequestBody BookCreateRequest request) {
        log.info("update book with request: " + request.toString());
        long start = System.currentTimeMillis();
        ResponseModel model = bookService.updateBook(currentUser, bookId, request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @DeleteMapping("/{book_id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    ResponseEntity<?> deleteBook(@CurrentUser UserPrincipal currentUser,
                                 @PathVariable(name = "book_id") Long bookId) {
        log.info("delete book");
        long start = System.currentTimeMillis();
        ResponseModel model = bookService.deleteBook(currentUser, bookId);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping("/crawl")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<?> crawlBooks(@RequestParam(name = "page") Integer page) {
        log.info("crawl data");
        long start = System.currentTimeMillis();
        ResponseModel model = bookService.crawlData(page);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }
}
