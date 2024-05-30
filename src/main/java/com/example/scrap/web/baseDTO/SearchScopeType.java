package com.example.scrap.web.baseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchScopeType {

    TITLE("title"),
    DESCRIPTION("description"),
    MEMO("memo");

    private String name;
}
