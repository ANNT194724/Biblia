package com.biblia.controller.issuinghouse;

import com.biblia.model.publisher.PublisherCreateRequest;
import com.biblia.model.response.ResponseModel;
import com.biblia.security.CurrentUser;
import com.biblia.security.UserPrincipal;
import com.biblia.service.issuinghouse.IssuingHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/issuing-house")
public class IssuingHouseController {

    @Autowired
    IssuingHouseService issuingHouseService;

    @PostMapping
    ResponseEntity<?> createIssuingHouse(@CurrentUser UserPrincipal currentUser,
                                         @RequestBody PublisherCreateRequest request) {
        log.info("create issuing house with request: " + request.toString());
        long start = System.currentTimeMillis();
        ResponseModel model = issuingHouseService.createPublisher(currentUser, request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping
    ResponseEntity<?> getIssuingHouse(@RequestParam(name = "page", required = false) Integer page,
                                      @RequestParam(name = "size", required = false) Integer size,
                                      @RequestParam(name = "name", required = false) String name) {
        log.info("get issuing houses");
        long start = System.currentTimeMillis();
        ResponseModel model = issuingHouseService.getIssuingHouses(page, size, name);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @GetMapping("/{issuing_house_id}")
    ResponseEntity<?> getPublisher(@PathVariable(name = "issuing_house_id") Integer issuingHouseId) {
        log.info("get issuing house");
        long start = System.currentTimeMillis();
        ResponseModel model = issuingHouseService.getIssuingHouse(issuingHouseId);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }

    @PutMapping("/{issuing_house_id}")
    ResponseEntity<?> updateIssuingHouse(@CurrentUser UserPrincipal currentUser,
                                         @PathVariable(name = "issuing_house_id") Integer issuingHouseId,
                                         @RequestBody PublisherCreateRequest request) {
        log.info("update issuing house with request: " + request.toString());
        long start = System.currentTimeMillis();
        ResponseModel model = issuingHouseService.updateIssuingHouse(currentUser, issuingHouseId, request);
        long end = System.currentTimeMillis();
        long diff = end - start;
        log.info("Code = " + model.getResponseStatus() + ", " + model.getDescription() + ", time = " + diff);
        return new ResponseEntity<>(model.getData(), model.getResponseStatus());
    }
}
