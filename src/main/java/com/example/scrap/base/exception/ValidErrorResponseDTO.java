package com.example.scrap.base.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ValidErrorResponseDTO {

    private String field;
    private String cause;
}
