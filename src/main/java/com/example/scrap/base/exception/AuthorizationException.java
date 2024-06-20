package com.example.scrap.base.exception;

import com.example.scrap.base.code.ErrorCode;
import lombok.Getter;

@Getter
public class AuthorizationException extends BaseException{

    public AuthorizationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
