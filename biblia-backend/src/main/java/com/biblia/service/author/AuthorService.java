package com.biblia.service.author;

import com.biblia.entity.Author;
import com.biblia.mapper.PagedResponseMapper;
import com.biblia.model.author.AuthorRequest;
import com.biblia.model.response.BaseModel;
import com.biblia.model.response.PagedResponse;
import com.biblia.model.response.ResponseModel;
import com.biblia.repository.author.AuthorRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AuthorService {

    @Autowired
    AuthorRepository authorRepository;

    public ResponseModel getAuthorList(String name, Integer page, Integer size) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            name = StringUtils.trimToEmpty(name);
            if (page == null || page <= 0)
                page = Constants.PAGINATION.DEFAULT_PAGE;
            if (size == null || size <= 0)
                size = Constants.PAGINATION.DEFAULT_SIZE;
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Author> authors = authorRepository.
                    findByNameContainsAndDeleteFlagOrderByName(name, Constants.DELETE_FLAG.NOT_DELETED, pageable);
            PagedResponse<?> pagedResponse = PagedResponseMapper.mapper(authors);
            message = "Get authors successfully";
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

    public ResponseModel getAuthor(Integer authorId) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            Author author = authorRepository.findByAuthorIdAndDeleteFlag(authorId, Constants.DELETE_FLAG.NOT_DELETED);
            if (Objects.isNull(author)) {
                message = "Author not found";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.NOT_FOUND);
                model.setData(new BaseModel(HttpStatus.NOT_FOUND.value(), message));
                return model;
            }
            message = "Get author successfully";
            model.setData(author);
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

    @Transactional
    public ResponseModel createAuthor(UserPrincipal currentUser, AuthorRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            HtmlUtil.validateRequest(request);
            Author author = new Author();
            author.setName(request.getName());
            author.setAlias(request.getAlias());
            author.setPhoto(request.getPhoto());
            author.setBorn(request.getBorn());
            author.setDied(request.getDied());
            author.setWebsite(request.getWebsite());
            author.setDescription(request.getDescription());
            if (Objects.equals(currentUser.getRoleCode(), Constants.ROLE_CODE.USER)) {
                author.setStatus(Constants.AUTHOR_STATUS.WAITING);
            } else {
                author.setStatus(Constants.AUTHOR_STATUS.VERIFIED);
            }
            author.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
            author.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
            author.setUpdatedUser(currentUser.getUserId());
            author.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
            authorRepository.save(author);
            message = "Create author successfully";
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

    public ResponseModel updateAuthor(UserPrincipal currentUser, Integer AuthorId, AuthorRequest request) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            HtmlUtil.validateRequest(request);
            Author author = authorRepository.findByAuthorIdAndDeleteFlag(AuthorId, Constants.DELETE_FLAG.NOT_DELETED);
            if (Objects.isNull(author)) {
                message = "author not found";
                model.setDescription(message);
                model.setResponseStatus(HttpStatus.NOT_FOUND);
                model.setData(new BaseModel(HttpStatus.NOT_FOUND.value(), message));
                return model;
            }
            author.setName(request.getName());
            author.setAlias(request.getAlias());
            author.setPhoto(request.getPhoto());
            author.setBorn(request.getBorn());
            author.setDied(request.getDied());
            author.setWebsite(request.getWebsite());
            author.setDescription(request.getDescription());
            author.setUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
            author.setUpdatedUser(currentUser.getUserId());
            author.setDeleteFlag(Constants.DELETE_FLAG.NOT_DELETED);
            authorRepository.save(author);
            message = "Update author successfully";
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
