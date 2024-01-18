package com.biblia.repository.review;

import com.biblia.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    @Query(value = "SELECT AVG(rating) FROM review " +
            "WHERE book_id = :book_id AND status = 1 AND delete_flag = 1", nativeQuery = true)
    Float getAverageRating(@Param("book_id") Long bookId);

    Page<Review> findByBookIdAndStatusAndDeleteFlagOrderByCreatedTimeDesc
            (Long bookId, Integer status, Integer deleteFlag, Pageable pageable);

    Page<Review> findByUserIdAndStatusAndDeleteFlagOrderByCreatedTimeDesc
            (Integer userId, Integer status, Integer deleteFlag, Pageable pageable);

    Review findByUserIdAndBookIdAndStatusAndDeleteFlag(Long userId, Long bookId, Integer status, Integer deleteFlag);
}
