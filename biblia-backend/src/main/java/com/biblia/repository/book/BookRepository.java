package com.biblia.repository.book;

import com.biblia.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByBookIdAndDeleteFlag(Long bookId, Integer deleteFlag);

    boolean existsByISBNAndDeleteFlag(String isbn, Integer deleteFlag);

    Book findByISBNAndDeleteFlag(String ISBN, Integer deleteFlag);

    Integer countByStatusAndDeleteFlag(Integer status, Integer deleteFlag);
}
