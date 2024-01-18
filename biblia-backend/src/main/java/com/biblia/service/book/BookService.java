package com.biblia.service.book;

import com.biblia.model.book.BookCreateRequest;
import com.biblia.model.response.ResponseModel;
import com.biblia.security.UserPrincipal;

import java.util.List;

public interface BookService {
    ResponseModel createBook(UserPrincipal currentUser, BookCreateRequest request);

    ResponseModel getBooks(Integer page, Integer size, String keyword, Integer authorId, Integer publisherId,
                           Integer issuingHouseId, Integer publishedYear, String sortBy, Integer sortDirection);

    ResponseModel getBookRequest(Integer page, Integer size);

    ResponseModel getBooksByGenre(List<Integer> genreIds, Integer page, Integer size);

    ResponseModel getBookDetail(Long bookId, Long userId);

    ResponseModel updateBook(UserPrincipal currentUser, Long bookId, BookCreateRequest request);

    ResponseModel deleteBook(UserPrincipal currentUser, Long bookId);

    ResponseModel crawlData(Integer page);
}
