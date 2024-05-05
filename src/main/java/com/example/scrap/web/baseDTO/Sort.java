package com.example.scrap.web.baseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Sort {

    TITLE("title"),
    SCRAP_DATE("createdAt");

    private String name;
}
