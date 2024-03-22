package com.biblia.repository.author;


import com.biblia.entity.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {
    void deleteAllByBookId(Long bookId);
}
