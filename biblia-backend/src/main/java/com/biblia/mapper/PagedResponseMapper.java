package com.biblia.mapper;

import com.biblia.model.response.PagedResponse;
import org.springframework.data.domain.Page;

public class PagedResponseMapper {
    public static PagedResponse<?> mapper(Page page){
        PagedResponse<?> pagedResponse = new PagedResponse<>();
        pagedResponse.setData(page.getContent());
        pagedResponse.setPage(page.getNumber()+1); //+1 do page tinh tu 0
        pagedResponse.setSize(page.getSize());
        pagedResponse.setTotalPages(page.getTotalPages());
        pagedResponse.setTotalElements(page.getTotalElements());
        pagedResponse.setLast(page.isLast());
        return  pagedResponse;
    }
}
