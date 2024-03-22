package com.biblia.mapper.series;

import com.biblia.model.series.SeriesResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SeriesResponseMapper implements RowMapper<SeriesResponse> {
    @Override
    public SeriesResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        SeriesResponse series = new SeriesResponse();
        series.setSeriesId(rs.getInt("series_id"));
        series.setTitle(rs.getString("title"));
        series.setIssuingHouseId(rs.getInt("issuing_house_id"));
        series.setVols(rs.getInt("vols"));
        series.setRating(rs.getFloat("score"));
        series.setDescription(rs.getString("description"));
        series.setStatus(rs.getInt("status"));
        return series;
    }
}
