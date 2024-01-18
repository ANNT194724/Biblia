package com.biblia.service.genre;

import com.biblia.entity.Genre;
import com.biblia.model.response.BaseModel;
import com.biblia.model.response.ResponseModel;
import com.biblia.repository.genre.GenreRepository;
import com.biblia.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GenreService {

    @Autowired
    GenreRepository genreRepository;

    public ResponseModel getGenres(String genre) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            genre = StringUtils.trimToEmpty(genre);
            List<Genre> genres = genreRepository
                    .findGenreByGenreContainsAndDeleteFlagOrderByGenre(genre, Constants.DELETE_FLAG.NOT_DELETED);
            message = "Get genres successfully";
            model.setData(genres);
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            return model;
        } catch (Exception e) {
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
    }

    public ResponseModel getGenre(Integer genreId) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            Genre genre = genreRepository.findByGenreIdAndDeleteFlag(genreId, Constants.DELETE_FLAG.NOT_DELETED);
            if (Objects.isNull(genre)) {
                message = "Genre not found";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.NOT_FOUND);
                model.setData(new BaseModel(HttpStatus.NOT_FOUND.value(), message));
                return model;
            }
            message = "Get genre successfully";
            model.setData(genre);
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            return model;
        } catch (Exception e) {
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
    }
}
