package com.biblia.repository.review;

import com.biblia.entity.Review;
import com.biblia.utils.Constants;
import org.springframework.data.jpa.domain.Specification;

public class ReviewSpecification {
    public static Specification<Review> searchReview(Long sellerId, Long bookId, Integer status) {
        return Specification.where(searchByUserId(sellerId)
                .and(searchByBookId(bookId))
                .and(searchByStatus(status))
                .and(searchByDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED)));
    }

    private static Specification<Review> searchByUserId(Long userId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (userId == null) return null;
            return criteriaBuilder.notEqual(root.get("userId"), userId);
        };
    }

    private static Specification<Review> searchByBookId(Long bookId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (bookId == null) return null;
            return criteriaBuilder.equal(root.get("bookId"), bookId);
        };
    }

    private static Specification<Review> searchByStatus(Integer status) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (status == null) return null;
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    private static Specification<Review> searchByDeleteFlag(Integer deleteFlag) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (deleteFlag == null) return null;
            return criteriaBuilder.equal(root.get("deleteFlag"), deleteFlag);
        };
    }
}
