package com.biblia.repository.genre;

import com.biblia.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    List<Genre> findGenreByGenreContainsAndDeleteFlagOrderByGenre(String genre, Integer deleteFlag);

    Genre findByGenreIdAndDeleteFlag(Integer genreId, Integer deleteFlag);
}
