package com.biblia.service.book;

import com.biblia.entity.BookGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookGenreRepository extends JpaRepository<BookGenre, Long> {
    void deleteAllByBookId(Long bookId);
}
