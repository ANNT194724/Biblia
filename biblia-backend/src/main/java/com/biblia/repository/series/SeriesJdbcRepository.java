package com.biblia.repository.series;

import com.biblia.mapper.series.AuthorResponseMapper;
import com.biblia.mapper.series.CoverResponseMapper;
import com.biblia.mapper.series.SeriesResponseMapper;
import com.biblia.model.series.AuthorResponse;
import com.biblia.model.series.CoverResponse;
import com.biblia.model.series.SeriesResponse;
import com.biblia.model.series.SeriesSearchRequest;
import com.biblia.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class SeriesJdbcRepository {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public Integer getTotalSeries(SeriesSearchRequest request) {
        try {
            String sql = generateSlqGetSeries(request, true);
            MapSqlParameterSource params = mapSqlParameterSeries(request);
            return jdbcTemplate.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    public List<SeriesResponse> getSeriesResponses(SeriesSearchRequest request) {
        try {
            String sql = generateSlqGetSeries(request, false);
            MapSqlParameterSource params = mapSqlParameterSeries(request);
            return jdbcTemplate.query(sql, params, new SeriesResponseMapper());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private MapSqlParameterSource mapSqlParameterSeries(SeriesSearchRequest request) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (!StringUtils.isBlank(request.getKeyword())) {
            if (StringUtils.containsWhitespace(request.getKeyword())) {
                params.addValue("keyword", request.getKeyword());
            } else {
                params.addValue("keyword", "%" + request.getKeyword() + "%");
            }
        }

        if (request.getAuthorId() != null)  {
            params.addValue("authorId", request.getAuthorId());
        }
        if (request.getIssuingHouseId() != null) {
            params.addValue("issuingHouseId", request.getIssuingHouseId());
        }
        params.addValue("deleteFlag", Constants.DELETE_FLAG.NOT_DELETED);
        params.addValue("limit", request.getSize());
        params.addValue("offset", (request.getPage() - 1) * request.getSize());
        return params;
    }

    private String generateSlqGetSeries(SeriesSearchRequest request, boolean count) {
        StringBuilder sql = new StringBuilder();
        if (count) {
            sql.append("SELECT COUNT(DISTINCT s.series_id) ");
        } else {
            sql.append("SELECT s.series_id, s.title, s.issuing_house_id, s.description, COUNT(DISTINCT book_id) AS vols, AVG(rating) AS score, s.status ");
        }
        sql.append("FROM series s LEFT JOIN book b ON s.series_id = b.series_id ");
        if (request.getAuthorId() != null)  {
            sql.append("JOIN series_author sa ON s.series_id = sa.series_id ");
        }
        sql.append("WHERE ");
        if (!StringUtils.isBlank(request.getKeyword())) {
            if (StringUtils.containsWhitespace(request.getKeyword())) {
                sql.append("(MATCH(s.title) AGAINST(:keyword IN natural language mode)" +
                        " OR MATCH(s.alias) AGAINST(:keyword IN natural language mode)) AND ");
            } else {
                sql.append("(s.title LIKE :keyword OR s.alias LIKE :keyword) AND ");
            }
        }
        if (request.getAuthorId() != null)  {
            sql.append("sa.author_id = :authorId AND ");
        }
        if (request.getIssuingHouseId() != null) {
            sql.append(("s.issuing_house_id = :issuingHouseId AND "));
        }
        sql.append("s.delete_flag = :deleteFlag ");
        if (!count) {
            sql.append("GROUP BY series_id ORDER BY s.created_time DESC LIMIT :limit OFFSET :offset");
        }
        return sql.toString();
    }

    public List<AuthorResponse> getAuthorBySeriesIds(List<Integer> seriesIds) {
        try {
            String sql = generateGetAuthorSql();
            MapSqlParameterSource params = mapSqlParameterAuthor(seriesIds);
            return jdbcTemplate.query(sql, params, new AuthorResponseMapper());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private String generateGetAuthorSql() {
        return "SELECT series_id, a.author_id, name, role " +
                "FROM author a JOIN series_author sa " +
                "ON a.author_id = sa.author_id " +
                "WHERE a.delete_flag = 1 AND sa.series_id IN (:seriesIds)";
    }

    private MapSqlParameterSource mapSqlParameterAuthor(List<Integer> seriesIds){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("seriesIds", seriesIds);
        return params;
    }

    public List<CoverResponse> getCoversBySeriesIds(List<Integer> seriesIds) {
        try {
            String sql = generateGetCoversSql();
            MapSqlParameterSource params = mapSqlParameterAuthor(seriesIds);
            return jdbcTemplate.query(sql, params, new CoverResponseMapper());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private String generateGetCoversSql() {
        return "SELECT series_id, GROUP_CONCAT(image_url ORDER BY book_id) AS covers " +
                "FROM book WHERE series_id > 0 AND series_id IN (:seriesIds) GROUP BY series_id;";
    }
}
