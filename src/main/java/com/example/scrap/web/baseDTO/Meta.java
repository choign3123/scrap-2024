package com.example.scrap.web.baseDTO;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class Meta {

    private int totalCount;
    private int currentPage;
    private Boolean isEnd;

    public Meta(Page page){
        this.totalCount = Long.valueOf(page.getTotalElements()).intValue();
        this.currentPage = page.getPageable().getPageNumber();
        this.isEnd = page.isLast();
    }
}
