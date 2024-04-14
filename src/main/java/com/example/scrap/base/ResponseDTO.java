package com.example.scrap.base;

import com.example.scrap.base.code.BaseCode;
import com.example.scrap.base.code.SuccessCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonPropertyOrder({"code", "message", "result"})
public class ResponseDTO<T>{

    @JsonIgnore
    private HttpStatus httpStatus;

    private String code;
    private String message;
    private T result;

    public ResponseDTO (T data){
        this.httpStatus = SuccessCode.OK.getHttpStatus();
        this.code = SuccessCode.OK.getCode();
        this.message = SuccessCode.OK.getMessage();
        this.result = data;
    }

    public  ResponseDTO(T data, BaseCode baseCode){
        this.httpStatus = baseCode.getHttpStatus();
        this.code = baseCode.getCode();
        this.message = baseCode.getMessage();
        this.result = data;
    }
}
