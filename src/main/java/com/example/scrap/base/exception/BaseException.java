package com.example.scrap.base.exception;

import com.example.scrap.base.code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{

    private ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.toString());
        this.errorCode = errorCode;
    }
}
