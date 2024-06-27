package com.example.scrap.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Sorts {

    TITLE("title"),
    SCRAP_DATE("createdAt");

    private String name;
}
