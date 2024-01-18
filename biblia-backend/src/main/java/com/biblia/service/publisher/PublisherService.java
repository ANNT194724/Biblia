package com.biblia.service.publisher;

import com.biblia.entity.Publisher;
import com.biblia.mapper.PagedResponseMapper;
import com.biblia.model.publisher.PublisherCreateRequest;
import com.biblia.model.response.BaseModel;
import com.biblia.model.response.PagedResponse;
import com.biblia.model.response.ResponseModel;
import com.biblia.repository.publisher.PublisherRepository;
import com.biblia.security.UserPrincipal;
import com.biblia.utils.Constants;
import com.biblia.utils.HtmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class PublisherService {

    @Autowired
    PublisherRepository publisherRepository;

    public ResponseModel createPublisher(UserPrincipal currentUser, PublisherCreateRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            HtmlUtil.validateRequest(request);
            Publisher publisher = new Publisher();
            publisher.setName(request.getName());
            publisher.setEmail(request.getEmail());
            publisher.setPhoneNumber(request.getPhoneNumber());
            publisher.setAddress(request.getAddress());
            publisher.setFacebook(request.getFacebook());
            publisher.setLogoUrl(request.getLogoUrl());
            publisher.setWebsite(request.getWebsite());
            publisher.setDescription(request.getDescription());
            publisher.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
            publisher.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
            publisher.setUpdatedUser(currentUser.getUserId());
            publisher.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
            publisherRepository.save(publisher);
            message = "Create publisher successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(new BaseModel(HttpStatus.OK.value(), message));
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

    public ResponseModel getPublishers(Integer page, Integer size, String name) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            name = StringUtils.trimToEmpty(name);
            if (page != null && size !=null) {
                Pageable pageable = PageRequest.of(page - 1, size);
                Page<Publisher> authors = publisherRepository.
                        findByNameContainsAndDeleteFlagOrderByName(name, Constants.DELETE_FLAG.NOT_DELETED, pageable);
                PagedResponse<?> pagedResponse = PagedResponseMapper.mapper(authors);
                model.setData(pagedResponse);
            } else {
                List<Publisher> publishers = publisherRepository.
                        findByNameContainsAndDeleteFlagOrderByName(name, Constants.DELETE_FLAG.NOT_DELETED);
                model.setData(publishers);
            }
            message = "Get publishers successfully";
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

    public ResponseModel getPublisher(Integer publisherId) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            Publisher publisher = publisherRepository
                    .findByPublisherIdAndDeleteFlag(publisherId, Constants.DELETE_FLAG.NOT_DELETED);
            if (Objects.isNull(publisher)) {
                message = "Publisher not found";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.NOT_FOUND);
                model.setData(new BaseModel(HttpStatus.NOT_FOUND.value(), message));
                return model;
            }
            message = "Get publisher successfully";
            model.setData(publisher);
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

    public ResponseModel updatePublisher(UserPrincipal currentUser, Integer publisherId, PublisherCreateRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            HtmlUtil.validateRequest(request);
            Publisher publisher = publisherRepository
                    .findByPublisherIdAndDeleteFlag(publisherId, Constants.DELETE_FLAG.NOT_DELETED);
            if (Objects.isNull(publisher)) {
                message = "Publisher not found";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.NOT_FOUND);
                model.setData(new BaseModel(HttpStatus.NOT_FOUND.value(), message));
                return model;
            }
            publisher.setName(request.getName());
            publisher.setEmail(request.getEmail());
            publisher.setPhoneNumber(request.getPhoneNumber());
            publisher.setAddress(request.getAddress());
            publisher.setFacebook(request.getFacebook());
            publisher.setLogoUrl(request.getLogoUrl());
            publisher.setWebsite(request.getWebsite());
            publisher.setDescription(request.getDescription());
            publisher.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
            publisher.setUpdatedUser(currentUser.getUserId());
            publisher.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
            publisherRepository.save(publisher);
            message = "Create publisher successfully";
            model.setDescription(message);
            model.setResponseStatus(HttpStatus.OK);
            model.setData(new BaseModel(HttpStatus.OK.value(), message));
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
