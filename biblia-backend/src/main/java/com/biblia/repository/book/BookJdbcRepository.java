package com.biblia.repository.book;

import com.biblia.mapper.author.AuthorResponseMapper;
import com.biblia.mapper.book.BookResponseMapper;
import com.biblia.model.book.AuthorResponse;
import com.biblia.model.book.BookResponse;
import com.biblia.model.book.BookSearchRequest;
import com.biblia.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Repository
public class BookJdbcRepository {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public Integer getTotalBooks(BookSearchRequest request) {
        try {
            String sql = generateSlqGetBooks(request, true);
            MapSqlParameterSource params = mapSqlParameterBooks(request);
            return jdbcTemplate.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    public List<BookResponse> getBookResponses(BookSearchRequest request) {
        try {
            String sql = generateSlqGetBooks(request, false);
            MapSqlParameterSource params = mapSqlParameterBooks(request);
            return jdbcTemplate.query(sql, params, new BookResponseMapper());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private MapSqlParameterSource mapSqlParameterBooks(BookSearchRequest request) {
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
        if (request.getPublisherId() != null) {
            params.addValue("publisherId", request.getPublisherId());
        }
        if (request.getIssuingHouseId() != null) {
            params.addValue("issuingHouseId", request.getIssuingHouseId());
        }
        if (request.getSeriesId() != null) {
            params.addValue("seriesId", request.getSeriesId());
        }
        params.addValue("status", request.getStatus());
        params.addValue("deleteFlag", Constants.DELETE_FLAG.NOT_DELETED);
        params.addValue("limit", request.getSize());
        params.addValue("offset", (request.getPage() - 1) * request.getSize());
        return params;
    }

    private String generateSlqGetBooks(BookSearchRequest request, boolean count) {
        StringBuilder sql = new StringBuilder();
        if (count) {
            sql.append("SELECT COUNT(DISTINCT b.book_id) ");
        } else {
            sql.append("SELECT b.book_id, ISBN, title, image_url, publisher_id, publisher, " +
                    "issuing_house_id, issuing_house, published_year, language, rating ");
        }
        String sortBy = request.getSortBy();
        Integer sortDirection = request.getSortDirection();
        sql.append("FROM book b ");
        if (request.getAuthorId() != null)  {
            sql.append("JOIN book_author ba ON b.book_id = ba.book_id ");
        }
        sql.append("WHERE ");
        if (!StringUtils.isBlank(request.getKeyword())) {
            if (StringUtils.containsWhitespace(request.getKeyword())) {
                sql.append("(MATCH(title) AGAINST(:keyword IN natural language mode)" +
                        " OR MATCH(alias) AGAINST(:keyword IN natural language mode)) AND ");
            } else {
                sql.append("(isbn LIKE :keyword OR title LIKE :keyword OR alias LIKE :keyword) AND ");
            }
        }
        if (request.getAuthorId() != null)  {
            sql.append("ba.author_id = :authorId AND ");
        }
        if (request.getPublisherId() != null) {
            sql.append("publisher_id = :publisherId AND ");
        }
        if (request.getIssuingHouseId() != null) {
            sql.append("issuing_house_id = :issuingHouseId AND ");
        }
        if (request.getSeriesId() != null) {
            sql.append("series_id = :seriesId AND ");
        }
        sql.append("status = :status AND b.delete_flag = :deleteFlag ");
         if (!count) {
             sql.append("ORDER BY ");
             switch (sortBy) {
                case Constants.SORT_BY.RATING -> sql.append("rating ");
                case Constants.SORT_BY.TITLE -> sql.append("LENGTH(title), title ");
             }
             switch (sortDirection) {
                case Constants.SORT_DIRECTION.ASC -> sql.append("ASC ");
                case Constants.SORT_DIRECTION.DESC -> sql.append("DESC ");
             }
             if (!StringUtils.isBlank(request.getKeyword()) && StringUtils.containsWhitespace(request.getKeyword())) {
                 sql.append(", b.created_time DESC ");
             }
            sql.append("LIMIT :limit OFFSET :offset");
        }
        return sql.toString();
    }

    public Integer getTotalBooksByGenre(List<Integer> genreIds, Integer page, Integer size) {
        try {
            String sql = generateSlqGetBooksByGenre(genreIds, true);
            MapSqlParameterSource params = mapSqlParameterBooksByGenre(genreIds, page, size);
            return jdbcTemplate.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    public List<BookResponse> getBookResponsesByGenre(List<Integer> genreIds, Integer page, Integer size) {
        try {
            String sql = generateSlqGetBooksByGenre(genreIds, false);
            MapSqlParameterSource params = mapSqlParameterBooksByGenre(genreIds, page, size);
            return jdbcTemplate.query(sql, params, new BookResponseMapper());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private MapSqlParameterSource mapSqlParameterBooksByGenre(List<Integer> genreIds, Integer page, Integer size) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("genreIds", genreIds);
        params.addValue("status", Constants.BOOK_STATUS.VERIFIED);
        params.addValue("deleteFlag", Constants.DELETE_FLAG.NOT_DELETED);
        params.addValue("limit", size);
        params.addValue("offset", (page - 1) * size);
        return params;
    }

    private String generateSlqGetBooksByGenre(List<Integer> genreIds, boolean count) {
        StringBuilder sql = new StringBuilder();
        if (count) {
            sql.append("SELECT COUNT(DISTINCT b.book_id) ");
        } else {
            sql.append("SELECT DISTINCT b.book_id, ISBN, title, image_url, publisher_id, publisher, " +
                    "issuing_house_id, issuing_house, published_year, language, rating, b.created_time ");
        }
        sql.append("FROM book b JOIN book_genre bg ON b.book_id = bg.book_id WHERE ");
        if (!CollectionUtils.isEmpty(genreIds)) {
            sql.append("bg.genre_id IN (:genreIds) AND ");
        }
        sql.append("status = :status AND b.delete_flag = :deleteFlag ");
        if (!count) {
            sql.append("ORDER BY rating DESC, b.created_time DESC LIMIT :limit OFFSET :offset");
        }
        return sql.toString();
    }

    public List<AuthorResponse> getAuthorByBookIds(List<Long> bookIds) {
        try {
            String sql = generateGetAuthorSql();
            MapSqlParameterSource params = mapSqlParameterAuthor(bookIds);
            return jdbcTemplate.query(sql, params, new AuthorResponseMapper());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private String generateGetAuthorSql() {
        return "SELECT book_id, a.author_id, name, role " +
                "FROM author a JOIN book_author ba " +
                "ON a.author_id = ba.author_id " +
                "WHERE a.delete_flag = 1 AND ba.book_id IN (:bookIds)";
    }

    private MapSqlParameterSource mapSqlParameterAuthor(List<Long> bookIds){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("bookIds",bookIds);
        return params;
    }

    public List<BookResponse> getBookAddRequest(Integer page, Integer size) {
        try {
            String sql = "SELECT DISTINCT book_id, ISBN, title, image_url, publisher_id, publisher, " +
                    "issuing_house_id, issuing_house, published_year, language, rating " +
                    "FROM book WHERE status = :status AND delete_flag = :deleteFlag " +
                    "LIMIT :limit OFFSET :offset";
            MapSqlParameterSource params = mapSqlParameterBookRequest(page, size);
            return jdbcTemplate.query(sql, params, new BookResponseMapper());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private MapSqlParameterSource mapSqlParameterBookRequest(Integer page, Integer size) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("status", Constants.BOOK_STATUS.WAITING);
        params.addValue("deleteFlag", Constants.DELETE_FLAG.NOT_DELETED);
        params.addValue("limit", size);
        params.addValue("offset", (page - 1) * size);
        return params;
    }
}
