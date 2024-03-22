package com.biblia.mapper.series;

import com.biblia.model.series.CoverResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CoverResponseMapper implements RowMapper<CoverResponse> {
    @Override
    public CoverResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        CoverResponse cover = new CoverResponse();
        cover.setSeriesId(rs.getInt("series_id"));
        String urls = rs.getString("covers");
        cover.setUrls(List.of(urls.split(",")));
        return cover;
    }
}
