package com.biblia.mapper.book;

import com.biblia.model.book.BookResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookResponseMapper implements RowMapper<BookResponse> {
    @Override
    public BookResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        BookResponse book = new BookResponse();
        book.setBookId(rs.getLong("book_id"));
        book.setISBN(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setImageUrl(rs.getString("image_url"));
        book.setPublisherId(rs.getInt("publisher_id"));
        book.setPublisher(rs.getString("publisher"));
        book.setIssuingHouseId(rs.getInt("issuing_house_id"));
        book.setIssuingHouse(rs.getString("issuing_house"));
        book.setPublishedYear(rs.getInt("published_year"));
        book.setLanguage(rs.getString("language"));
        book.setRating(rs.getFloat("rating"));
        return book;
    }
}
