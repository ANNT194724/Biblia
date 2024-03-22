package com.biblia.controller.series;

import com.biblia.model.response.ResponseModel;
import com.biblia.model.series.SeriesRequest;
import com.biblia.security.CurrentUser;
import com.biblia.security.UserPrincipal;
import com.biblia.service.series.SeriesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/series")
public class SeriesController {

    @Autowired
    SeriesService seriesService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    ResponseEntity<?> createBook(@CurrentUser UserPrincipal currentUser,
                                 @RequestBody SeriesRequest request) {
        log.info("create series with request: " + request.toString());
        long start = System.currentTimeMillis();
        ResponseModel model = seriesService.addSeries(currentUser, request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping
    ResponseEntity<?> getSeries(@RequestParam(name = "page") Integer page,
                                @RequestParam(name = "size") Integer size,
                                @RequestParam(name = "keyword", required = false) String keyword,
                                @RequestParam(name = "author_id", required = false) Integer authorId,
                                @RequestParam(name = "issuing_house", required = false) Integer issuingHouse) {
        log.info("get series");
        long start = System.currentTimeMillis();
        ResponseModel model = seriesService.getSeries(page, size, authorId, issuingHouse, keyword);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping("/{series_id}")
    ResponseEntity<?> getSeriesDetail(@PathVariable(name = "series_id") Integer seriesId) {
        log.info("get series detail");
        long start = System.currentTimeMillis();
        ResponseModel model = seriesService.getSeriesDetail(seriesId);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @PutMapping("/{series_id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    ResponseEntity<?> updateBook(@CurrentUser UserPrincipal currentUser,
                                 @PathVariable("series_id") Integer seriesId,
                                 @RequestBody SeriesRequest request) {
        log.info("update series with request: " + request.toString());
        long start = System.currentTimeMillis();
        ResponseModel model = seriesService.updateSeries(currentUser, seriesId, request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }
}
