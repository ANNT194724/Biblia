package com.biblia.service.review;

import com.biblia.entity.Book;
import com.biblia.entity.Review;
import com.biblia.mapper.PagedResponseMapper;
import com.biblia.model.review.ReviewRequest;
import com.biblia.model.response.BaseModel;
import com.biblia.model.response.PagedResponse;
import com.biblia.model.response.ResponseModel;
import com.biblia.repository.book.BookRepository;
import com.biblia.repository.review.ReviewRepository;
import com.biblia.repository.review.ReviewSpecification;
import com.biblia.security.UserPrincipal;
import com.biblia.utils.Constants;
import com.biblia.utils.HtmlUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ReviewService {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    BookRepository bookRepository;

    @Transactional
    public ResponseModel createReview(UserPrincipal currentUser, ReviewRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            HtmlUtil.validateRequest(request);
            Book book = bookRepository.findByBookIdAndDeleteFlag(request.getBookId(), Constants.DELETE_FLAG.NOT_DELETED);
            if (book == null) {
                message = "Book not found";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.NOT_FOUND);
                model.setData(new BaseModel(HttpStatus.NOT_FOUND.value(), message));
                return model;
            }
            Review review = new Review();
            review.setBookId(request.getBookId());
            review.setUserId(currentUser.getUserId());
            review.setUsername(currentUser.getUsername());
            review.setRating(request.getRating());
            review.setContent(request.getContent());
            review.setStatus(Constants.REVIEW_STATUS.NOT_HIDDEN);
            review.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
            review.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
            review.setUpdatedUser(currentUser.getUserId());
            review.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
            reviewRepository.save(review);
            Float newRating = reviewRepository.getAverageRating(book.getBookId());
            if (!Objects.isNull(newRating)) {
                book.setRating(newRating);
                bookRepository.save(book);
            }
            message = "Create review successfully";
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

    public ResponseModel getReviews(Integer page, Integer size, Long bookId, Long userId) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            if (page == null || page <= 0)
                page = Constants.PAGINATION.DEFAULT_PAGE;
            if (size == null || size <= 0)
                size = Constants.PAGINATION.DEFAULT_SIZE;
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Review> reviewPage = reviewRepository.findAll
                    (ReviewSpecification.searchReview(userId, bookId, Constants.REVIEW_STATUS.NOT_HIDDEN), pageable);
            PagedResponse<?> pagedResponse = PagedResponseMapper.mapper(reviewPage);
            message = "Get reviews successfully";
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
}
