package com.biblia.service.series;

import com.biblia.entity.Series;
import com.biblia.entity.SeriesAuthor;
import com.biblia.model.response.BaseModel;
import com.biblia.model.response.PagedResponse;
import com.biblia.model.response.ResponseModel;
import com.biblia.model.series.*;
import com.biblia.repository.book.BookRepository;
import com.biblia.repository.series.SeriesAuthorRepository;
import com.biblia.repository.series.SeriesJdbcRepository;
import com.biblia.repository.series.SeriesRepository;
import com.biblia.security.UserPrincipal;
import com.biblia.utils.Constants;
import com.biblia.utils.HtmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SeriesService {

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    SeriesAuthorRepository seriesAuthorRepository;

    @Autowired
    SeriesJdbcRepository jdbcRepository;

    @Autowired
    BookRepository bookRepository;

    @Transactional
    public ResponseModel addSeries(UserPrincipal currentUser, SeriesRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            HtmlUtil.validateRequest(request);
            Series series = new Series();
            series.setTitle(request.getTitle());
            series.setAlias(request.getAlias());
            series.setIssuingHouseId(request.getIssuingHouseId());
            series.setIssuingHouse(request.getIssuingHouse());
            series.setDescription(request.getDescription());
            series.setStatus(request.getStatus());
            Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
            series.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
            series.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
            series.setUpdatedUser(currentUser.getUserId());
            series.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
            seriesRepository.save(series);
            if (!CollectionUtils.isEmpty(request.getSeriesAuthors())) {
                List<SeriesAuthor> seriesAuthorList = new ArrayList<>();
                request.getSeriesAuthors().forEach(author -> {
                    SeriesAuthor seriesAuthor = new SeriesAuthor();
                    seriesAuthor.setSeriesId(series.getSeriesId());
                    seriesAuthor.setAuthorId(author.getAuthorId());
                    seriesAuthor.setRole(author.getRole());
                    seriesAuthor.setCreatedTime(currentTime);
                    seriesAuthorList.add(seriesAuthor);
                });
                seriesAuthorRepository.saveAll(seriesAuthorList);
            }
            bookRepository.addToSeries(series.getSeriesId(), request.getTitle(), request.getBookIds());
            message = "Series added successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(series);
            return model;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
    }

    public ResponseModel getSeries(int page, int size, Integer authorId, Integer issuingHouseId, String keyword) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            SeriesSearchRequest request = new SeriesSearchRequest();
            if (page > 0)
                request.setPage(page);
            else
                request.setPage(Constants.PAGINATION.DEFAULT_PAGE);
            request.setSize(size);
            request.setAuthorId(authorId);
            request.setKeyword(StringUtils.trimToEmpty(keyword));
            request.setIssuingHouseId(issuingHouseId);
            Integer count = jdbcRepository.getTotalSeries(request);
            PagedResponse<SeriesResponse> pagedResponse = new PagedResponse<>();
            List<SeriesResponse> seriesList = jdbcRepository.getSeriesResponses(request);
            if (count == 0 || CollectionUtils.isEmpty(seriesList)) {
                pagedResponse.setData(new ArrayList<>());
                pagedResponse.setPage(request.getPage());
                pagedResponse.setTotalPages(0);
                pagedResponse.setTotalElements(count);
                pagedResponse.setSize(request.getSize());
                pagedResponse.setLast(true);
                model.setData(pagedResponse);
                model.setResponseStatus(HttpStatus.OK);
                model.setDescription("Get series response successfully");
                return model;
            }
            List<Integer> seriesIdList = seriesList.stream().map(SeriesResponse::getSeriesId).toList();
            List<AuthorResponse> authorList = jdbcRepository.getAuthorBySeriesIds(seriesIdList);
            Map<Integer, List<AuthorResponse>> authorMap = authorList.stream()
                    .collect(Collectors.groupingBy(AuthorResponse::getSeriesId));
            seriesList.forEach(series -> series.setAuthors(authorMap.get(series.getSeriesId())));
            List<CoverResponse> coverList = jdbcRepository.getCoversBySeriesIds(seriesIdList);
            Map<Integer, CoverResponse> coversMap = coverList.stream()
                    .collect(Collectors.toMap(CoverResponse::getSeriesId, Function.identity()));
            seriesList.forEach(series -> {
                CoverResponse covers = coversMap.get(series.getSeriesId());
                if (!Objects.isNull(covers)) series.setCovers(covers.getUrls().stream().limit(3).toList());
            });
            int totalPage = 0;
            if (request.getSize() > 0) {
                totalPage = count / request.getSize();
                int diff = count % request.getSize();
                if (diff != 0) totalPage = totalPage + 1;
            }
            pagedResponse.setData(seriesList);
            pagedResponse.setPage(request.getPage());
            pagedResponse.setTotalPages(totalPage);
            pagedResponse.setTotalElements(count);
            pagedResponse.setSize(request.getSize());
            pagedResponse.setLast(request.getPage() == totalPage);
            message = "Get series list successfully";
            model.setData(pagedResponse);
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

    public ResponseModel getSeriesDetail(Integer seriesId) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            Series series = seriesRepository.findBySeriesIdAndDeleteFlag(seriesId, Constants.DELETE_FLAG.NOT_DELETED);
            if (Objects.isNull(series)) {
                message = "Series not found";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.NOT_FOUND);
                model.setData(new BaseModel(HttpStatus.NOT_FOUND.value(), message));
                return model;
            }
//            List<com.biblia.model.series.AuthorResponse> authors = bookJdbcRepository.getAuthorByBookIds(Collections.singletonList(bookId));
//            Review review = reviewRepository.findByUserIdAndBookIdAndStatusAndDeleteFlag
//                    (userId, bookId, Constants.REVIEW_STATUS.NOT_HIDDEN, Constants.DELETE_FLAG.NOT_DELETED);
//            BookDetailResponse bookDetail = new BookDetailResponse(series, authors, review);
            message = "Get series detail successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(series);
            return model;
        } catch (Exception e) {
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
    }

    @Transactional
    public ResponseModel updateSeries(UserPrincipal currentUser, Integer seriesId, SeriesRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            HtmlUtil.validateRequest(request);
            Series series = seriesRepository.findBySeriesIdAndDeleteFlag(seriesId, Constants.DELETE_FLAG.NOT_DELETED);
            series.setTitle(request.getTitle());
            series.setAlias(request.getAlias());
            series.setIssuingHouseId(request.getIssuingHouseId());
            series.setIssuingHouse(request.getIssuingHouse());
            series.setDescription(request.getDescription());
            series.setStatus(request.getStatus());
            Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
            series.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
            series.setUpdatedUser(currentUser.getUserId());
            seriesRepository.save(series);
            if (!CollectionUtils.isEmpty(request.getSeriesAuthors())) {
                seriesAuthorRepository.deleteAllBySeriesId(seriesId);
                List<SeriesAuthor> seriesAuthorList = new ArrayList<>();
                request.getSeriesAuthors().forEach(author -> {
                    SeriesAuthor seriesAuthor = new SeriesAuthor();
                    seriesAuthor.setSeriesId(series.getSeriesId());
                    seriesAuthor.setAuthorId(author.getAuthorId());
                    seriesAuthor.setRole(author.getRole());
                    seriesAuthor.setCreatedTime(currentTime);
                    seriesAuthorList.add(seriesAuthor);
                });
                seriesAuthorRepository.saveAll(seriesAuthorList);
            }
            List<Long> bookIds = bookRepository.getBookIdsBySeries(seriesId);
            if (!new HashSet<>(bookIds).containsAll(request.getBookIds())) {
                List<Long> idsToAdd = request.getBookIds().stream().filter(id -> !bookIds.contains(id)).toList();
                if (!CollectionUtils.isEmpty(idsToAdd))
                    bookRepository.addToSeries(seriesId, series.getTitle(), idsToAdd);
                List<Long> idsToRemove = bookIds.stream().filter(id -> !request.getBookIds().contains(id)).toList();
                if (!CollectionUtils.isEmpty(idsToRemove))
                    bookRepository.addToSeries(null, "", idsToRemove);
            }
            message = "Series updated successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(series);
            return model;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            message = e.getMessage();
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            model.setData(new BaseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
            return model;
        }
    }
}
