package com.example.scrap.base.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseCode{

    OK(HttpStatus.OK, "OK001", "요청에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
