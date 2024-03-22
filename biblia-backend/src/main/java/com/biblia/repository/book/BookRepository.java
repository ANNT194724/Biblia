package com.biblia.repository.book;

import com.biblia.entity.Book;
import com.biblia.model.book.BookView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByBookIdAndDeleteFlag(Long bookId, Integer deleteFlag);

    boolean existsByISBNAndDeleteFlag(String isbn, Integer deleteFlag);

    Book findByISBNAndDeleteFlag(String ISBN, Integer deleteFlag);

    Integer countByStatusAndDeleteFlag(Integer status, Integer deleteFlag);

    @Query(value = "SELECT isbn FROM book WHERE delete_flag = 1", nativeQuery = true)
    List<String> getActiveISBN();

    @Query(value = "SELECT title FROM book WHERE delete_flag = 1", nativeQuery = true)
    List<String> getActiveTitle();

    @Modifying
    @Query(value = "UPDATE book SET series_id = :seriesId, series = :series WHERE book_id IN (:bookIds)", nativeQuery = true)
    void addToSeries(@Param("seriesId") Integer seriesId, @Param("series") String series, @Param("bookIds") List<Long> bookIds);

    @Query(value = "SELECT book_id FROM book WHERE series_id = :seriesId", nativeQuery = true)
    List<Long> getBookIdsBySeries(@Param("seriesId") Integer seriesId);

    @Query(value = "SELECT book_id AS bookId, title FROM book " +
            "WHERE title LIKE CONCAT('%', :keyword, '%') AND status = 1 AND delete_flag = 1", nativeQuery = true)
    List<BookView> getBookViews(@Param("keyword") String keyword);
}
