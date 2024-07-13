package com.example.scrap.web.baseDTO;

import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class Meta {

    private long totalElemnt; // 총 테이터 개수
    private int numOfElement; // 현재 페이지에 나올 데이터 수
    private Boolean isEnd;

    public Meta(Page page){
        this.totalElemnt = page.getTotalElements();
        this.numOfElement = page.getNumberOfElements();
        this.isEnd = page.isLast();
    }
}
