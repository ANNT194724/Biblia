package com.biblia.service.book;

import com.biblia.entity.Book;
import com.biblia.entity.BookAuthor;
import com.biblia.entity.BookGenre;
import com.biblia.entity.Review;
import com.biblia.model.book.*;
import com.biblia.model.response.BaseModel;
import com.biblia.model.response.PagedResponse;
import com.biblia.model.response.ResponseModel;
import com.biblia.repository.book.BookJdbcRepository;
import com.biblia.repository.book.BookRepository;
import com.biblia.repository.review.ReviewRepository;
import com.biblia.security.UserPrincipal;
import com.biblia.utils.Constants;
import com.biblia.utils.HtmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    BookAuthorRepository bookAuthorRepository;

    @Autowired
    BookGenreRepository bookGenreRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BookJdbcRepository bookJdbcRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Override
    @Transactional
    public ResponseModel createBook(UserPrincipal currentUser, BookCreateRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        HtmlUtil.validateRequest(request);
        try {
            if (!StringUtils.isBlank(request.getISBN())) {
                Book book = bookRepository.findByISBNAndDeleteFlag(request.getISBN(), Constants.DELETE_FLAG.NOT_DELETED);
                if (!Objects.isNull(book)) {
                    message = "This ISBN already existed in the database";
                    model.setDescription(message);
                    model.setResponseStatus(HttpStatus.BAD_REQUEST);
                    model.setData(new BaseModel(HttpStatus.BAD_REQUEST.value(), message));
                    return model;
                }
            }
            Book book = new Book();
            book.setISBN(request.getISBN());
            book.setTitle(request.getTitle());
            book.setImageUrl(request.getImageUrl());
            book.setAlias(request.getAlias());
            book.setPublisherId(request.getPublisherId());
            book.setPublisher(request.getPublisher());
            book.setIssuingHouseId(request.getIssuingHouseId());
            book.setIssuingHouse(request.getIssuingHouse());
            book.setLanguage(request.getLanguage());
            book.setPagesNo(request.getPagesNo());
            book.setDescription(request.getDescription());
            book.setRating((float) 0);
            if (Objects.equals(currentUser.getRoleCode(), Constants.ROLE_CODE.USER)) {
                book.setStatus(Constants.BOOK_STATUS.WAITING);
            } else {
                book.setStatus(Constants.BOOK_STATUS.VERIFIED);
            }
            book.setPublishedYear(request.getPublishedYear());
            Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
            book.setCreatedTime(currentTime);
            book.setUpdatedTime(currentTime);
            book.setUpdatedUser(currentUser.getUserId());
            book.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
            bookRepository.save(book);
            if (!CollectionUtils.isEmpty(request.getBookAuthors())) {
                List<BookAuthor> bookAuthorList = new ArrayList<>();
                request.getBookAuthors().forEach(author -> {
                    BookAuthor bookAuthor = new BookAuthor();
                    bookAuthor.setBookId(book.getBookId());
                    bookAuthor.setAuthorId(author.getAuthorId());
                    bookAuthor.setRole(author.getRole());
                    bookAuthor.setCreatedTime(currentTime);
                    bookAuthor.setUpdatedTime(currentTime);
                    bookAuthor.setUpdatedUser(currentUser.getUserId());
                    bookAuthor.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                    bookAuthorList.add(bookAuthor);
                });
                bookAuthorRepository.saveAll(bookAuthorList);
            }
            if (!CollectionUtils.isEmpty(request.getGenreIds())) {
                List<BookGenre> bookGenreList = new ArrayList<>();
                request.getGenreIds().forEach(genreId -> {
                    BookGenre bookGenre = new BookGenre();
                    bookGenre.setBookId(book.getBookId());
                    bookGenre.setGenreId(genreId);
                    bookGenre.setCreatedTime(currentTime);
                    bookGenre.setUpdatedTime(currentTime);
                    bookGenre.setUpdatedUser(currentUser.getUserId());
                    bookGenre.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                    bookGenreList.add(bookGenre);
                });
                bookGenreRepository.saveAll(bookGenreList);
            }
            message = "Book created successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(book);
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

    @Override
    public ResponseModel getBooks(Integer page, Integer size, String keyword, Integer authorId, Integer publisherId,
                                  Integer issuingHouseId, Integer publishedYear, String sortBy, Integer sortDirection) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            BookSearchRequest request = new BookSearchRequest();
            if (page > 0)
                request.setPage(page);
            else
                request.setPage(Constants.PAGINATION.DEFAULT_PAGE);
            request.setSize(size);
            request.setAuthorId(authorId);
            request.setKeyword(StringUtils.trimToEmpty(keyword));
            request.setPublisherId(publisherId);
            request.setIssuingHouseId(issuingHouseId);
            request.setStatus(Constants.BOOK_STATUS.VERIFIED);
            request.setSortBy(sortBy);
            request.setSortDirection(sortDirection);
            Integer count = bookJdbcRepository.getTotalBooks(request);
            PagedResponse<BookResponse> pagedResponse = new PagedResponse<>();
            List<BookResponse> bookList = bookJdbcRepository.getBookResponses(request);
            if (count == 0 || CollectionUtils.isEmpty(bookList)) {
                pagedResponse.setData(new ArrayList<>());
                pagedResponse.setPage(request.getPage());
                pagedResponse.setTotalPages(0);
                pagedResponse.setTotalElements(count);
                pagedResponse.setSize(request.getSize());
                pagedResponse.setLast(true);
                model.setData(pagedResponse);
                model.setResponseStatus(HttpStatus.OK);
                model.setDescription("Get book response successfully");
                return model;
            }
            List<Long> bookIdList = bookList.stream().map(BookResponse::getBookId).toList();
            List<AuthorResponse> authorList = bookJdbcRepository.getAuthorByBookIds(bookIdList);
            Map<Long, List<AuthorResponse>> authorMap = authorList.stream()
                    .collect(Collectors.groupingBy(AuthorResponse::getBookId));
            bookList.forEach(book -> book.setAuthors(authorMap.get(book.getBookId())));
            int totalPage = 0;
            if (request.getSize() > 0) {
                totalPage = count / request.getSize();
                int diff = count % request.getSize();
                if (diff != 0) totalPage = totalPage + 1;
            }
            pagedResponse.setData(bookList);
            pagedResponse.setPage(request.getPage());
            pagedResponse.setTotalPages(totalPage);
            pagedResponse.setTotalElements(count);
            pagedResponse.setSize(request.getSize());
            pagedResponse.setLast(request.getPage() == totalPage);
            message = "Get book list successfully";
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

    @Override
    public ResponseModel getBookRequest(Integer page, Integer size) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            if (page < 0)
                page = Constants.PAGINATION.DEFAULT_PAGE;
            Integer count = bookRepository.countByStatusAndDeleteFlag
                    (Constants.BOOK_STATUS.WAITING, Constants.DELETE_FLAG.NOT_DELETED);
            PagedResponse<BookResponse> pagedResponse = new PagedResponse<>();
            List<BookResponse> bookList = bookJdbcRepository.getBookAddRequest(page, size);
            if (count == 0 || CollectionUtils.isEmpty(bookList)) {
                pagedResponse.setData(new ArrayList<>());
                pagedResponse.setPage(page);
                pagedResponse.setTotalPages(0);
                pagedResponse.setTotalElements(count);
                pagedResponse.setSize(size);
                pagedResponse.setLast(true);
                model.setData(pagedResponse);
                model.setResponseStatus(HttpStatus.OK);
                model.setDescription("Get book response successfully");
                return model;
            }
            List<Long> bookIdList = bookList.stream().map(BookResponse::getBookId).toList();
            List<AuthorResponse> authorList = bookJdbcRepository.getAuthorByBookIds(bookIdList);
            Map<Long, List<AuthorResponse>> authorMap = authorList.stream()
                    .collect(Collectors.groupingBy(AuthorResponse::getBookId));
            bookList.forEach(book -> book.setAuthors(authorMap.get(book.getBookId())));
            int totalPage = 0;
            if (size > 0) {
                totalPage = count / size;
                int diff = count % size;
                if (diff != 0) totalPage = totalPage + 1;
            }
            pagedResponse.setData(bookList);
            pagedResponse.setPage(page);
            pagedResponse.setTotalPages(totalPage);
            pagedResponse.setTotalElements(count);
            pagedResponse.setSize(size);
            pagedResponse.setLast(page == totalPage);
            message = "Get book list successfully";
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


    @Override
    public ResponseModel getBooksByGenre(List<Integer> genreIds, Integer page, Integer size) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            if (page < 0)
                page = Constants.PAGINATION.DEFAULT_PAGE;
            Integer count = bookJdbcRepository.getTotalBooksByGenre(genreIds, page, size);
            PagedResponse<BookResponse> pagedResponse = new PagedResponse<>();
            List<BookResponse> bookList = bookJdbcRepository.getBookResponsesByGenre(genreIds, page, size);
            if (count == 0 || CollectionUtils.isEmpty(bookList)) {
                pagedResponse.setData(new ArrayList<>());
                pagedResponse.setPage(page);
                pagedResponse.setTotalPages(0);
                pagedResponse.setTotalElements(count);
                pagedResponse.setSize(size);
                pagedResponse.setLast(true);
                model.setData(pagedResponse);
                model.setResponseStatus(HttpStatus.OK);
                model.setDescription("Get book response successfully");
                return model;
            }
            List<Long> bookIdList = bookList.stream().map(BookResponse::getBookId).toList();
            List<AuthorResponse> authorList = bookJdbcRepository.getAuthorByBookIds(bookIdList);
            Map<Long, List<AuthorResponse>> authorMap = authorList.stream()
                    .collect(Collectors.groupingBy(AuthorResponse::getBookId));
            bookList.forEach(book -> book.setAuthors(authorMap.get(book.getBookId())));
            int totalPage = 0;
            if (size > 0) {
                totalPage = count / size;
                int diff = count % size;
                if (diff != 0) totalPage = totalPage + 1;
            }
            pagedResponse.setData(bookList);
            pagedResponse.setPage(page);
            pagedResponse.setTotalPages(totalPage);
            pagedResponse.setTotalElements(count);
            pagedResponse.setSize(size);
            pagedResponse.setLast(page == totalPage);
            message = "Get book list successfully";
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

    @Override
    public ResponseModel getBookDetail(Long bookId, Long userId) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            Book book = bookRepository.findByBookIdAndDeleteFlag(bookId, Constants.DELETE_FLAG.NOT_DELETED);
            if (Objects.isNull(book)) {
                message = "Book not found";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.NOT_FOUND);
                model.setData(new BaseModel(HttpStatus.NOT_FOUND.value(), message));
                return model;
            }
            List<AuthorResponse> authors = bookJdbcRepository.getAuthorByBookIds(Collections.singletonList(bookId));
            Review review = reviewRepository.findByUserIdAndBookIdAndStatusAndDeleteFlag
                    (userId, bookId, Constants.REVIEW_STATUS.NOT_HIDDEN, Constants.DELETE_FLAG.NOT_DELETED);
            BookDetailResponse bookDetail = new BookDetailResponse(book, authors, review);
            message = "Get book detail successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(bookDetail);
            return model;
        } catch (Exception e) {
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
    }

    @Override
    @Transactional
    public ResponseModel updateBook(UserPrincipal currentUser, Long bookId, BookCreateRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        HtmlUtil.validateRequest(request);
        try {
            Book book = bookRepository.findByBookIdAndDeleteFlag(bookId, Constants.DELETE_FLAG.NOT_DELETED);
            if (Objects.isNull(book)) {
                message = "Book not found";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.NOT_FOUND);
                model.setData(new BaseModel(HttpStatus.NOT_FOUND.value(), message));
                return model;
            }
            if (!StringUtils.isBlank(request.getISBN()) && !StringUtils.equals(request.getISBN(), book.getISBN())) {
                Book isbn = bookRepository.findByISBNAndDeleteFlag(request.getISBN(), Constants.DELETE_FLAG.NOT_DELETED);
                if (!Objects.isNull(isbn)) {
                    message = "This ISBN already existed in the database";
                    model.setDescription(message);
                    model.setResponseStatus(HttpStatus.BAD_REQUEST);
                    model.setData(new BaseModel(HttpStatus.BAD_REQUEST.value(), message));
                    return model;
                }
            }
            book.setISBN(request.getISBN());
            book.setTitle(request.getTitle());
            book.setAlias(request.getAlias());
            book.setImageUrl(request.getImageUrl());
            book.setPublisherId(request.getPublisherId());
            book.setPublisher(request.getPublisher());
            book.setIssuingHouseId(request.getIssuingHouseId());
            book.setIssuingHouse(request.getIssuingHouse());
            book.setLanguage(request.getLanguage());
            book.setPagesNo(request.getPagesNo());
            book.setDescription(request.getDescription());
            book.setPublishedYear(request.getPublishedYear());
            Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
            book.setUpdatedTime(currentTime);
            book.setUpdatedUser(currentUser.getUserId());
            book.setStatus(Constants.BOOK_STATUS.VERIFIED);
            bookRepository.save(book);
            bookAuthorRepository.deleteAllByBookId(bookId);
            if (!CollectionUtils.isEmpty(request.getBookAuthors())) {
                List<BookAuthor> bookAuthorList = new ArrayList<>();
                request.getBookAuthors().forEach(author -> {
                    BookAuthor bookAuthor = new BookAuthor();
                    bookAuthor.setBookId(book.getBookId());
                    bookAuthor.setAuthorId(author.getAuthorId());
                    bookAuthor.setRole(author.getRole());
                    bookAuthor.setCreatedTime(currentTime);
                    bookAuthor.setUpdatedTime(currentTime);
                    bookAuthor.setUpdatedUser(currentUser.getUserId());
                    bookAuthor.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                    bookAuthorList.add(bookAuthor);
                });
                bookAuthorRepository.saveAll(bookAuthorList);
            }
            bookGenreRepository.deleteAllByBookId(bookId);
            if (!CollectionUtils.isEmpty(request.getGenreIds())) {
                List<BookGenre> bookGenreList = new ArrayList<>();
                request.getGenreIds().forEach(genreId -> {
                    BookGenre bookGenre = new BookGenre();
                    bookGenre.setBookId(book.getBookId());
                    bookGenre.setGenreId(genreId);
                    bookGenre.setCreatedTime(currentTime);
                    bookGenre.setUpdatedTime(currentTime);
                    bookGenre.setUpdatedUser(currentUser.getUserId());
                    bookGenre.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                    bookGenreList.add(bookGenre);
                });
                bookGenreRepository.saveAll(bookGenreList);
            }
            message = "Book created successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(book);
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

    @Override
    public ResponseModel deleteBook(UserPrincipal currentUser, Long bookId) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            Book book = bookRepository.findByBookIdAndDeleteFlag(bookId, Constants.DELETE_FLAG.NOT_DELETED);
            if (book == null) {
                message = "Book not found";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.NOT_FOUND);
                model.setData(new BaseModel(HttpStatus.NOT_FOUND.value(), message));
                return model;
            }
            book.setDeleteFlag(Constants.DELETE_FLAG.DELETED);
            bookRepository.save(book);
            message = "Book updated successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(book);
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

    private int extractPageNumber(String releaseText) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*trang");
        Matcher matcher = pattern.matcher(releaseText);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return 0; // Default value if no match is found
    }

    @Transactional
    public ResponseModel crawlData(Integer page) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            List<Book> books = new ArrayList<>();
            String url = "https://kenhgames.com/volume?page=" + page;
            Document document = Jsoup.connect(url).get();
            Elements bookElements = document.getElementsByClass("tg-postbook");
            for (Element bookElement : bookElements) {
                Book book = new Book();
                String title = bookElement.select(".tg-booktitle h3 a").attr("title");
                Element isbnElement = bookElement.select(".tg-booktranslator a").first();
                String isbn = isbnElement != null ? isbnElement.text().replaceAll("ISBN: ", "").replaceAll("-", "") : "";
                if (bookRepository.existsByISBNAndDeleteFlag(isbn, Constants.DELETE_FLAG.NOT_DELETED))
                    continue;
                Element releaseElement = bookElement.select(".tg-release a").first();
                String pagesNo = releaseElement != null ? releaseElement.text() : "";
                Element frontCoverElement = bookElement.select(".tg-frontcover img").first();
                String imageUrl = frontCoverElement != null ? frontCoverElement.attr("src") : "";
                book.setISBN(isbn);
                book.setTitle(title);
                book.setImageUrl(imageUrl);
                book.setPagesNo(extractPageNumber(pagesNo));
                book.setRating((float) 0);
                book.setPublishedYear(2023);
                book.setLanguage("Tiếng Việt");
                book.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
                book.setStatus(Constants.BOOK_STATUS.WAITING);
                book.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                books.add(book);
            }
            bookRepository.saveAll(books);
            message = "Book updated successfully";
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
