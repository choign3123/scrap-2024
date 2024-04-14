package com.example.scrap.base.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {

    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}
