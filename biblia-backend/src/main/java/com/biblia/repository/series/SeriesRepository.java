package com.biblia.repository.series;

import com.biblia.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Integer> {
    Series findBySeriesIdAndDeleteFlag(Integer seriesId, Integer deleteFlag);
}
