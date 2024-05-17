package com.example.scrap.base.exception;

import com.example.scrap.base.code.ErrorCode;
import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException{

    private String filed;
    private String causes;
    private ErrorCode errorCode;

    public ValidationException(String filed, String causes){
        this(filed, causes, ErrorCode._BAD_REQUEST);
    }

    public ValidationException(String filed, String causes, ErrorCode errorCode){
        super(filed + ": " + causes);
        this.filed = filed;
        this.causes = causes;
        this.errorCode = errorCode;
    }
}
