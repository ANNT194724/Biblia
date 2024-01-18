package com.biblia.service.issuinghouse;

import com.biblia.entity.IssuingHouse;
import com.biblia.mapper.PagedResponseMapper;
import com.biblia.model.publisher.PublisherCreateRequest;
import com.biblia.model.response.BaseModel;
import com.biblia.model.response.PagedResponse;
import com.biblia.model.response.ResponseModel;
import com.biblia.repository.issuinghouse.IssuingHouseRepository;
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
public class IssuingHouseService {
    @Autowired
    IssuingHouseRepository issuingHouseRepository;

    public ResponseModel createPublisher(UserPrincipal currentUser, PublisherCreateRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            HtmlUtil.validateRequest(request);
            IssuingHouse issuingHouse = new IssuingHouse();
            issuingHouse.setName(request.getName());
            issuingHouse.setEmail(request.getEmail());
            issuingHouse.setPhoneNumber(request.getPhoneNumber());
            issuingHouse.setAddress(request.getAddress());
            issuingHouse.setFacebook(request.getFacebook());
            issuingHouse.setLogoUrl(request.getLogoUrl());
            issuingHouse.setWebsite(request.getWebsite());
            issuingHouse.setDescription(request.getDescription());
            issuingHouse.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
            issuingHouse.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
            issuingHouse.setUpdatedUser(currentUser.getUserId());
            issuingHouse.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
            issuingHouseRepository.save(issuingHouse);
            message = "Create issuing house successfully";
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

    public ResponseModel getIssuingHouses(Integer page, Integer size, String name) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            name = StringUtils.trimToEmpty(name);
            if (page != null && size !=null) {
                Pageable pageable = PageRequest.of(page - 1, size);
                Page<IssuingHouse> authors = issuingHouseRepository.
                        findByNameContainsAndDeleteFlagOrderByName(name, Constants.DELETE_FLAG.NOT_DELETED, pageable);
                PagedResponse<?> pagedResponse = PagedResponseMapper.mapper(authors);
                model.setData(pagedResponse);
            } else {
                List<IssuingHouse> publishers = issuingHouseRepository.
                        findByNameContainsAndDeleteFlagOrderByName(name, Constants.DELETE_FLAG.NOT_DELETED);
                model.setData(publishers);
            }
            message = "Get issuing houses successfully";
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

    public ResponseModel getIssuingHouse(Integer IssuingHouseId) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            IssuingHouse issuingHouse = issuingHouseRepository
                    .findByIssuingHouseIdAndDeleteFlag(IssuingHouseId, Constants.DELETE_FLAG.NOT_DELETED);
            if (Objects.isNull(issuingHouse)) {
                message = "Issuing house not found";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.NOT_FOUND);
                model.setData(new BaseModel(HttpStatus.NOT_FOUND.value(), message));
                return model;
            }
            message = "Get issuingHouse successfully";
            model.setData(issuingHouse);
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

    public ResponseModel updatePublisher(UserPrincipal currentUser, Integer issuingHouseId, PublisherCreateRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            HtmlUtil.validateRequest(request);
            IssuingHouse issuingHouse = issuingHouseRepository
                    .findByIssuingHouseIdAndDeleteFlag(issuingHouseId, Constants.DELETE_FLAG.NOT_DELETED);
            if (Objects.isNull(issuingHouse)) {
                message = "Issuing house not found";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.NOT_FOUND);
                model.setData(new BaseModel(HttpStatus.NOT_FOUND.value(), message));
                return model;
            }
            issuingHouse.setName(request.getName());
            issuingHouse.setEmail(request.getEmail());
            issuingHouse.setPhoneNumber(request.getPhoneNumber());
            issuingHouse.setAddress(request.getAddress());
            issuingHouse.setFacebook(request.getFacebook());
            issuingHouse.setLogoUrl(request.getLogoUrl());
            issuingHouse.setWebsite(request.getWebsite());
            issuingHouse.setDescription(request.getDescription());
            issuingHouse.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
            issuingHouse.setUpdatedUser(currentUser.getUserId());
            issuingHouse.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
            issuingHouseRepository.save(issuingHouse);
            message = "Create issuing house successfully";
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
