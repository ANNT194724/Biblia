package com.biblia.repository.series;

import com.biblia.entity.SeriesAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesAuthorRepository extends JpaRepository<SeriesAuthor, Long> {
    void deleteAllBySeriesId(Integer seriesId);
}
