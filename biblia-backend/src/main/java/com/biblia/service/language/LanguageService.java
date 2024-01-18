package com.biblia.service.language;

import com.biblia.entity.Language;
import com.biblia.model.response.BaseModel;
import com.biblia.model.response.ResponseModel;
import com.biblia.repository.language.LanguageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {

    @Autowired
    LanguageRepository languageRepository;

    public ResponseModel getLanguage(String name) {
        ResponseModel model = new ResponseModel();
        String message;
        try {
            name = StringUtils.trimToEmpty(name);
            List<Language> languages = languageRepository.findLanguageByNameContainsOrLocalContains(name, name);
            message = "Get languages successfully";
            model.setData(languages);
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
