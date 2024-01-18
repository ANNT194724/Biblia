package com.biblia.repository.author;

import com.biblia.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Page<Author> findByNameContainsAndDeleteFlagOrderByName(String name, Integer DeleteFlag, Pageable pageable);

    Author findByAuthorIdAndDeleteFlag(Integer authorId, Integer deleteFlag);
}
