package com.example.scrap.base.enums;

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
