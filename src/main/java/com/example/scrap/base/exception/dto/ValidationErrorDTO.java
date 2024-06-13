package com.example.scrap.base.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ValidationErrorDTO {

    private String field;
    private String reason;
}
