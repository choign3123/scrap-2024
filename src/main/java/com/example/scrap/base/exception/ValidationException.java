package com.example.scrap.base.exception;

import com.example.scrap.base.code.ErrorCode;
import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException{

    private String filed;
    private String reason;
    private ErrorCode errorCode;

    public ValidationException(String filed, String reason){
        this(filed, reason, ErrorCode._BAD_REQUEST);
    }

    public ValidationException(String filed, String reason, ErrorCode errorCode){
        super(filed + ": " + reason);
        this.filed = filed;
        this.reason = reason;
        this.errorCode = errorCode;
    }
}
