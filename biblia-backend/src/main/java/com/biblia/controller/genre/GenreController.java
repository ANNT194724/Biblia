package com.biblia.controller.genre;

import com.biblia.model.response.ResponseModel;
import com.biblia.service.genre.GenreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/genre")
public class GenreController {

    @Autowired
    GenreService genreService;

    @GetMapping
    ResponseEntity<?> getGenres(@RequestParam(name = "genre", required = false) String genre) {
        log.info("get genres");
        long start = System.currentTimeMillis();
        ResponseModel model = genreService.getGenres(genre);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping("/{genre_id}")
    ResponseEntity<?> getGenre(@PathVariable("genre_id") Integer genreId) {
        log.info("get genre");
        long start = System.currentTimeMillis();
        ResponseModel model = genreService.getGenre(genreId);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }
}
