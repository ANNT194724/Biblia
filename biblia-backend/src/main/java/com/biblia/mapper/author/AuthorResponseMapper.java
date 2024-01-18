package com.biblia.mapper.author;

import com.biblia.model.book.AuthorResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorResponseMapper implements RowMapper<AuthorResponse> {

    @Override
    public AuthorResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthorResponse author = new AuthorResponse();
        author.setBookId(rs.getLong("book_id"));
        author.setAuthorId(rs.getInt("author_id"));
        author.setName(rs.getString("name"));
        author.setRole(rs.getString("role"));
        return author;
    }
}
