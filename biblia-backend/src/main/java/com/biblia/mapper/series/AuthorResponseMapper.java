package com.biblia.mapper.series;

import com.biblia.model.series.AuthorResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorResponseMapper implements RowMapper<AuthorResponse> {
    @Override
    public AuthorResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthorResponse author = new AuthorResponse();
        author.setSeriesId(rs.getInt("series_id"));
        author.setAuthorId(rs.getInt("author_id"));
        author.setName(rs.getString("name"));
        author.setRole(rs.getString("role"));
        return author;
    }
}
