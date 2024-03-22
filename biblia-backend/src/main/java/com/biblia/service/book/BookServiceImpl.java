package com.biblia.service.book;

import com.biblia.entity.*;
import com.biblia.model.book.*;
import com.biblia.model.response.BaseModel;
import com.biblia.model.response.PagedResponse;
import com.biblia.model.response.ResponseModel;
import com.biblia.repository.author.BookAuthorRepository;
import com.biblia.repository.book.BookJdbcRepository;
import com.biblia.repository.book.BookRepository;
import com.biblia.repository.genre.BookGenreRepository;
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
                                  Integer issuingHouseId, Integer seriesId, String sortBy, Integer sortDirection) {
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
            request.setSeriesId(seriesId);
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
    public ResponseModel getBookViews(String keyword) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            List<BookView> bookViews = bookRepository.getBookViews(keyword);
            message = "Get book detail successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(bookViews);
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
            if (!CollectionUtils.isEmpty(request.getBookAuthors())) {
                bookAuthorRepository.deleteAllByBookId(bookId);
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
                bookGenreRepository.deleteAllByBookId(bookId);
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

    private static String extractInfo(Element element, String label) {
        if (element == null) return "";
        Element infoElement = element.selectFirst("li:contains(" + label + ")");
        if (infoElement == null) {
            infoElement = element.selectFirst("span:contains(" + label + ")");
        }
        if (infoElement != null) {
            return infoElement.text().replace(label, "").replace("-", "").trim();
        }
        return "";
    }

    @Transactional
    public ResponseModel crawlDataKimDong(Integer page) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            List<Book> books = new ArrayList<>();
            String baseUrl = "https://nxbkimdong.com.vn";
//            String url = baseUrl + "/collections/wings?page=" + page;
            String url = "https://nxbkimdong.com.vn/search?q=filter=(titlespace:product**reboot)&type=product";
            Document document = Jsoup.connect(url).get();
            Elements bookElements = document.getElementsByClass("product-item");
            List<String> isbnList = bookRepository.getActiveISBN();
            for (Element bookElement : bookElements) {
                Book book = new Book();
                Element imgElement = bookElement.getElementsByClass("product-img").first();
                String title = imgElement.select("img").attr("alt");
                String detailUrl = baseUrl + imgElement.select("a").attr("href");
                if (StringUtils.equals(baseUrl, detailUrl)) continue;
                Document detailDocument = Jsoup.connect(detailUrl).get();
                Element descElement = detailDocument.getElementsByClass("pro-short-desc").first();
                String isbn = extractInfo(descElement, "ISBN:");
                String imageUrl = detailDocument.getElementsByClass("product-single__thumbnail").first().attr("href");
                if (isbnList.contains(isbn)) {
                    continue;
                }
                String pagesNo = extractInfo(descElement, "Số trang:");
                Element contentElement = detailDocument.getElementsByClass("pro-tabcontent").first();
                book.setISBN(isbn);
                book.setTitle(title);
                book.setImageUrl(imageUrl);
                book.setPagesNo(Integer.valueOf(pagesNo));
                book.setRating((float) 0);
                book.setPublishedYear(2023);
                book.setPublisherId(6);
                book.setPublisher("Nhà xuất bản Kim Đồng");
                book.setAlias("");
//                book.setIssuingHouseId(6);
//                book.setIssuingHouse("Kim Đồng - Wings Books");
                book.setLanguage("Tiếng Việt");
                book.setDescription(contentElement.html());
                book.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
                book.setStatus(Constants.BOOK_STATUS.WAITING);
                book.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
//                System.out.print("* ");
//                System.out.println(book);
                books.add(book);
                isbnList.add(isbn);
            }
            bookRepository.saveAll(books);
            List<BookAuthorRequest> danmachiAuthors = new ArrayList<>();
            danmachiAuthors.add(new BookAuthorRequest(179, "Tác giả"));
            danmachiAuthors.add(new BookAuthorRequest(180, "Minh họa"));
//            danmachiAuthors.add(new BookAuthorRequest(72, "Nguyên tác"));
            danmachiAuthors.add(new BookAuthorRequest(181, "Dịch giả"));
            books.forEach(book -> {
                if (!CollectionUtils.isEmpty(danmachiAuthors)) {
                    List<BookAuthor> bookAuthorList = new ArrayList<>();
                    danmachiAuthors.forEach(author -> {
                        BookAuthor bookAuthor = new BookAuthor();
                        bookAuthor.setBookId(book.getBookId());
                        bookAuthor.setAuthorId(author.getAuthorId());
                        bookAuthor.setRole(author.getRole());
                        bookAuthor.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
                        bookAuthor.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
                        bookAuthor.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                        bookAuthorList.add(bookAuthor);
                    });
                    bookAuthorRepository.saveAll(bookAuthorList);
                }
                List<Integer> genreIds = Arrays.asList(6, 46, 1, 21);
                List<BookGenre> bookGenreList = new ArrayList<>();
                genreIds.forEach(genreId -> {
                    BookGenre bookGenre = new BookGenre();
                    bookGenre.setBookId(book.getBookId());
                    bookGenre.setGenreId(genreId);
                    bookGenre.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
                    bookGenre.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
                    bookGenre.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                    bookGenreList.add(bookGenre);
                });
                bookGenreRepository.saveAll(bookGenreList);
            });
            message = books.size() + " books crawled successfully";
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

    @Transactional
    public ResponseModel crawlDataIPM(Integer page) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            List<Book> books = new ArrayList<>();
            String baseUrl = "https://ipm.vn";
//            String url = baseUrl + "/collections/wings?page=" + page;
            String url = "https://ipm.vn/search?type=product&q=r%E1%BB%93i+hoa+s%E1%BA%BD+n%E1%BB%9F";
            Document document = Jsoup.connect(url).get();
            Elements bookElements = document.getElementsByClass("product-block product-resize");
            List<String> titleList = bookRepository.getActiveTitle();
            for (Element bookElement : bookElements) {
                Book book = new Book();
                Element titleElement = bookElement.getElementsByClass("pro-name").first();
                String title = titleElement.selectFirst("a").attr("title");
                if (StringUtils.isBlank(title) || titleList.contains(title)) continue;
                String detailUrl = baseUrl + titleElement.select("a").attr("href");
                Document detailDocument = Jsoup.connect(detailUrl).get();
                Element descElement = detailDocument.getElementsByClass("box-tag").first();
                String year = descElement.selectFirst("div:contains(Nhà xuất bản) + div span").ownText();
                String imageUrl = detailDocument.getElementsByClass("product-image-feature-slider").first().selectFirst("a").attr("href");
//                String pagesNo = descElement.select("td.data_qty_of_page").first().text();
                Element contentElement = detailDocument.getElementsByClass("short-desc").first();
                book.setISBN("");
                book.setTitle(title);
                book.setImageUrl(imageUrl);
//                book.setPagesNo(Integer.valueOf(pagesNo));
                book.setRating((float) 0);
                book.setPublishedYear(2023);
                book.setPublisherId(41);
                book.setPublisher("Nhà xuất bản Hà Nội");
                book.setAlias("Yagate Kimi ni Naru");
                book.setIssuingHouseId(1);
                book.setIssuingHouse("IPM");
                book.setLanguage("Tiếng Việt");
                book.setDescription(contentElement.html().replace("<strong>Nội dung: </strong>\n", ""));
                book.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
                book.setStatus(Constants.BOOK_STATUS.WAITING);
                book.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
//                System.out.print("* ");
//                System.out.println(book);
                books.add(book);
            }
            bookRepository.saveAll(books);
            List<BookAuthorRequest> danmachiAuthors = new ArrayList<>();
            danmachiAuthors.add(new BookAuthorRequest(117, "Tác giả"));
            danmachiAuthors.add(new BookAuthorRequest(118, "Dịch giả"));
            books.forEach(book -> {
                if (!CollectionUtils.isEmpty(danmachiAuthors)) {
                    List<BookAuthor> bookAuthorList = new ArrayList<>();
                    danmachiAuthors.forEach(author -> {
                        BookAuthor bookAuthor = new BookAuthor();
                        bookAuthor.setBookId(book.getBookId());
                        bookAuthor.setAuthorId(author.getAuthorId());
                        bookAuthor.setRole(author.getRole());
                        bookAuthor.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
                        bookAuthor.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
                        bookAuthor.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                        bookAuthorList.add(bookAuthor);
                    });
                    bookAuthorRepository.saveAll(bookAuthorList);
                }
                List<Integer> genreIds = Arrays.asList(6, 36, 26);
                List<BookGenre> bookGenreList = new ArrayList<>();
                genreIds.forEach(genreId -> {
                    BookGenre bookGenre = new BookGenre();
                    bookGenre.setBookId(book.getBookId());
                    bookGenre.setGenreId(genreId);
                    bookGenre.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
                    bookGenre.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
                    bookGenre.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                    bookGenreList.add(bookGenre);
                });
                bookGenreRepository.saveAll(bookGenreList);
            });
            message = books.size() + " books crawled successfully";
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

    @Transactional
    public ResponseModel crawlDataTre(Integer page) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            List<Book> books = new ArrayList<>();
            String baseUrl = "https://www.nxbtre.com.vn";
            String url = "https://www.nxbtre.com.vn/tac-gia/atsushi-ohkubo-66529.html";
            Document document = Jsoup.connect(url).get();
            Elements bookElements = document.getElementsByClass("absolute books");
            List<String> isbnList = bookRepository.getActiveISBN();
            for (Element bookElement : bookElements) {
                Book book = new Book();
                Element imgElement = bookElement.getElementsByClass("imgBook").first();
                String title = imgElement.attr("alt");
                title = title.substring(0,1).toUpperCase() + title.substring(1).toLowerCase();
                String imageUrl = baseUrl + imgElement.attr("src");
                String detailUrl = baseUrl + bookElement.select("a").attr("href");
                if (StringUtils.equals(baseUrl, detailUrl)) continue;
                Document detailDocument = Jsoup.connect(detailUrl).get();
                Element descElement = detailDocument.getElementsByClass("itemDetail-cat").first();
                Element isbnElement = descElement.selectFirst("li:contains(ISBN)");
                if (isbnElement == null)
                    continue;
                String isbn = isbnElement.selectFirst("span").ownText().replace("-", "");
                if (isbnList.contains(isbn)) {
                    continue;
                }
                String pagesNo = descElement.selectFirst("li:contains(Số trang)").selectFirst("span").ownText().replace(",", "");
                String desc = descElement.selectFirst("li:contains(Giới thiệu tóm tắt tác phẩm)").html();
                book.setISBN(isbn);
                book.setTitle(title);
                book.setImageUrl(imageUrl);
                book.setPagesNo(Integer.valueOf(pagesNo));
                book.setRating((float) 0);
                book.setPublishedYear(2023);
                book.setPublisherId(42);
                book.setPublisher("Nhà xuất bản Trẻ");
                book.setAlias("Enen no Shouboutai");
                book.setLanguage("Tiếng Việt");
                book.setDescription(desc);
                book.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
                book.setStatus(Constants.BOOK_STATUS.WAITING);
                book.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
//                System.out.print("* ");
//                System.out.println(book);
                books.add(book);
                isbnList.add(isbn);
            }
            bookRepository.saveAll(books);
            List<BookAuthorRequest> authors = new ArrayList<>();
            authors.add(new BookAuthorRequest(152, "Tác giả"));
//            authors.add(new BookAuthorRequest(136, "Minh họa"));
            authors.add(new BookAuthorRequest(12, "Dịch giả"));
            books.forEach(book -> {
                if (!CollectionUtils.isEmpty(authors)) {
                    List<BookAuthor> bookAuthorList = new ArrayList<>();
                    authors.forEach(author -> {
                        BookAuthor bookAuthor = new BookAuthor();
                        bookAuthor.setBookId(book.getBookId());
                        bookAuthor.setAuthorId(author.getAuthorId());
                        bookAuthor.setRole(author.getRole());
                        bookAuthor.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
                        bookAuthor.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
                        bookAuthor.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                        bookAuthorList.add(bookAuthor);
                    });
                    bookAuthorRepository.saveAll(bookAuthorList);
                }
                List<Integer> genreIds = Arrays.asList(6, 5, 1);
                List<BookGenre> bookGenreList = new ArrayList<>();
                genreIds.forEach(genreId -> {
                    BookGenre bookGenre = new BookGenre();
                    bookGenre.setBookId(book.getBookId());
                    bookGenre.setGenreId(genreId);
                    bookGenre.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
                    bookGenre.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
                    bookGenre.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                    bookGenreList.add(bookGenre);
                });
                bookGenreRepository.saveAll(bookGenreList);
            });
            message = books.size() + " books crawled successfully";
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

    @Transactional
    public ResponseModel crawlDataAmak(Integer page) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            List<Book> books = new ArrayList<>();
            String baseUrl = "https://amakstore.vn";
            String url = "https://amakstore.vn/search?type=product&q=t%C3%B4i%20y%C3%AAu%20n%E1%BB%AF%20ph%E1%BA%A3n%20di%E1%BB%87n";
            Document document = Jsoup.connect(url).get();
            Elements bookElements = document.getElementsByClass("product-inner");
            List<String> isbnList = bookRepository.getActiveISBN();
            for (Element bookElement : bookElements) {
                Book book = new Book();
                Element titleElement = bookElement.getElementsByClass("proloop-detail").select("h3").select("a").first();
                String title = titleElement.text();
                String detailUrl = baseUrl + titleElement.attr("href");
                if (StringUtils.equals(baseUrl, detailUrl)) continue;
                Document detailDocument = Jsoup.connect(detailUrl).get();
                Element descElement = detailDocument.getElementsByClass("description-productdetail").first();
                Element isbnElement = descElement.selectFirst("li:contains(ISBN)");
                if (isbnElement == null)
                    continue;
                String isbn = isbnElement.ownText().replace("ISBN: ", "").replace("-", "");
                if (isbnList.contains(isbn)) {
                    continue;
                }
                String pagesNo = descElement.selectFirst("li:contains(Số trang)").ownText().replace("Số trang: ", "").replace(" trang", "");
                String imageUrl = detailDocument.getElementsByClass("product-gallery__item").first().attr("href");
                Elements desc = descElement.select("p");
                StringBuilder description = new StringBuilder();
                for (Element element : desc) {
                    description.append(element.html());
                }
                book.setISBN(isbn);
                book.setTitle(title);
                book.setImageUrl(imageUrl);
                book.setPagesNo(Integer.valueOf(pagesNo));
                book.setRating((float) 0);
                book.setPublishedYear(2023);
                book.setPublisherId(12);
                book.setPublisher("Nhà xuất bản Dân trí");
                book.setIssuingHouseId(2);
                book.setIssuingHouse("Amak");
                book.setAlias("Watashi no Oshi wa Akuyaku Reijou");
                book.setLanguage("Tiếng Việt");
                book.setDescription(description.toString().replace("Giới thiệu:", ""));
                book.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
                book.setStatus(Constants.BOOK_STATUS.WAITING);
                book.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                System.out.print("* ");
                System.out.println(book);
                books.add(book);
                isbnList.add(isbn);
            }
            bookRepository.saveAll(books);
            List<BookAuthorRequest> authors = new ArrayList<>();
            authors.add(new BookAuthorRequest(172, "Tác giả"));
            authors.add(new BookAuthorRequest(173, "Minh họa"));
            authors.add(new BookAuthorRequest(174, "Dịch giả"));
            books.forEach(book -> {
                if (!CollectionUtils.isEmpty(authors)) {
                    List<BookAuthor> bookAuthorList = new ArrayList<>();
                    authors.forEach(author -> {
                        BookAuthor bookAuthor = new BookAuthor();
                        bookAuthor.setBookId(book.getBookId());
                        bookAuthor.setAuthorId(author.getAuthorId());
                        bookAuthor.setRole(author.getRole());
                        bookAuthor.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
                        bookAuthor.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
                        bookAuthor.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                        bookAuthorList.add(bookAuthor);
                    });
                    bookAuthorRepository.saveAll(bookAuthorList);
                }
                List<Integer> genreIds = Arrays.asList(7, 36, 5, 1);
                List<BookGenre> bookGenreList = new ArrayList<>();
                genreIds.forEach(genreId -> {
                    BookGenre bookGenre = new BookGenre();
                    bookGenre.setBookId(book.getBookId());
                    bookGenre.setGenreId(genreId);
                    bookGenre.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
                    bookGenre.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
                    bookGenre.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
                    bookGenreList.add(bookGenre);
                });
                bookGenreRepository.saveAll(bookGenreList);
            });
            message = books.size() + " books crawled successfully";
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
