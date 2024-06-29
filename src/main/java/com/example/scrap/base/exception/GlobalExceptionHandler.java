package com.example.scrap.base.exception;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.dto.ValidationErrorDTO;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.ErrorConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // [TODO] HttpRequestMethodNotSupportedException

    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ResponseDTO> handleBaseException(BaseException e){
        log.info(e.getMessage());
        e.printStackTrace();

        ResponseDTO<Void> response = new ResponseDTO<>(e.getErrorCode());

        return ResponseEntity.status(response.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(AuthorizationException.class)
    protected ResponseEntity<ResponseDTO> handleAuthorizationException(AuthorizationException e){
        log.info(e.getMessage());

        ResponseDTO<Void> response = new ResponseDTO<>(e.getErrorCode());

        return ResponseEntity.status(response.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseDTO> handleException(Exception e){
        log.error(e.getMessage());
        e.printStackTrace();

        ResponseDTO<Void> response = new ResponseDTO<>(ErrorCode._INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(response.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        e.printStackTrace();

        BindingResult bindingResult = e.getBindingResult();
        ResponseDTO<ValidationErrorDTO> response = new ResponseDTO<>(ErrorConverter.toValidationErrorDTO(bindingResult), ErrorCode._BAD_REQUEST);

        return ResponseEntity.status(response.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ResponseDTO> handleConstraintViolationException(ConstraintViolationException e){
        e.printStackTrace();

        Set<ConstraintViolation<?>> cv = e.getConstraintViolations();
        ResponseDTO<ValidationErrorDTO> response = new ResponseDTO<>(ErrorConverter.toValidationErrorDTO(cv), ErrorCode._BAD_REQUEST);

        return ResponseEntity.status(response.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ResponseDTO> handleMissingServletRequestParameterException(MissingServletRequestParameterException e){
        e.printStackTrace();

        ResponseDTO<ValidationErrorDTO> response = new ResponseDTO<>(new ValidationErrorDTO(e.getParameterName(), "필수값입니다."), ErrorCode._BAD_REQUEST);

        return ResponseEntity.status(response.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ResponseDTO> handleMissingRequestHeaderException(MissingRequestHeaderException e){
        e.printStackTrace();

        ResponseDTO<ValidationErrorDTO> response = new ResponseDTO<>(new ValidationErrorDTO(e.getHeaderName(), "필수값입니다."), ErrorCode._BAD_REQUEST);

        return ResponseEntity.status(response.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<ResponseDTO> handleValidationException(ValidationException e){

        ResponseDTO<ValidationErrorDTO> response = new ResponseDTO<>(ErrorConverter.toValidationErrorDTO(e), e.getErrorCode());

        return ResponseEntity.status(response.getHttpStatus())
                .body(response);
    }
}
