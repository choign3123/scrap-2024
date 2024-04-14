package com.example.scrap.base.exception;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.response.ApiResponse;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.ErrorConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    protected ApiResponse handleBaseException(BaseException e){
        log.info(e.getMessage());

        ResponseDTO<Void> responseDTO = new ResponseDTO<>(e.getErrorCode());
        return new ApiResponse(responseDTO);
    }

    @ExceptionHandler(Exception.class)
    protected ApiResponse handleBaseException(Exception e){
        log.error(e.getMessage());
        e.printStackTrace();

        ResponseDTO<String> responseDTO = new ResponseDTO<>(ErrorCode._INTERNAL_SERVER_ERROR);
        return new ApiResponse(responseDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ApiResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        e.printStackTrace();

        BindingResult bindingResult = e.getBindingResult();
        ResponseDTO<ValidErrorResponseDTO> responseDTO = new ResponseDTO<>(ErrorConverter.toValidErrorResponseDTO(bindingResult), ErrorCode._BAD_REQUEST);
        return new ApiResponse(responseDTO);
    }
}
