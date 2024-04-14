package com.example.scrap.base.response;

import com.example.scrap.base.code.BaseCode;
import com.example.scrap.base.code.SuccessCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    private final HttpStatus httpStatus;

    private final String code;
    private final String message;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private final T result;

    public ResponseDTO (){
        this.httpStatus = SuccessCode.OK.getHttpStatus();
        this.code = SuccessCode.OK.getCode();
        this.message = SuccessCode.OK.getMessage();
        this.result = null;
    }

    public ResponseDTO (T data){
        this.httpStatus = SuccessCode.OK.getHttpStatus();
        this.code = SuccessCode.OK.getCode();
        this.message = SuccessCode.OK.getMessage();
        this.result = data;
    }

    public ResponseDTO (BaseCode baseCode){
        this.httpStatus = baseCode.getHttpStatus();
        this.code = baseCode.getCode();
        this.message = baseCode.getMessage();
        this.result = null;
    }

    public  ResponseDTO(T data, BaseCode baseCode){
        this.httpStatus = baseCode.getHttpStatus();
        this.code = baseCode.getCode();
        this.message = baseCode.getMessage();
        this.result = data;
    }
}
