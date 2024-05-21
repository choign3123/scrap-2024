package com.example.scrap.base.exception;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.response.ApiResponse;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.ErrorConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

    @ExceptionHandler(BaseException.class)
    protected ApiResponse handleBaseException(BaseException e){
        log.info(e.getMessage());

        ResponseDTO<Void> responseDTO = new ResponseDTO<>(e.getErrorCode());
        return new ApiResponse(responseDTO);
    }

    @ExceptionHandler(Exception.class)
    protected ApiResponse handleException(Exception e){
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

    @ExceptionHandler(ConstraintViolationException.class)
    protected ApiResponse handleConstraintViolationException(ConstraintViolationException e){
        e.printStackTrace();

        Set<ConstraintViolation<?>> cv = e.getConstraintViolations();
        ResponseDTO<ValidErrorResponseDTO> responseDTO = new ResponseDTO<>(ErrorConverter.toValidErrorResponseDTO(cv), ErrorCode._BAD_REQUEST);
        return new ApiResponse(responseDTO);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ApiResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException e){
        e.printStackTrace();

        ValidErrorResponseDTO response = new ValidErrorResponseDTO(e.getParameterName(), "필수값입니다.");
        return new ApiResponse(new ResponseDTO(response, ErrorCode._BAD_REQUEST));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ApiResponse handleMissingRequestHeaderException(MissingRequestHeaderException e){
        e.printStackTrace();

        ValidErrorResponseDTO response = new ValidErrorResponseDTO(e.getHeaderName(), "필수값입니다.");
        return new ApiResponse(new ResponseDTO(response, ErrorCode._BAD_REQUEST));
    }

    @ExceptionHandler(ValidationException.class)
    protected ApiResponse handleValidationException(ValidationException e){

        ResponseDTO<ValidErrorResponseDTO> responseDTO = new ResponseDTO<>(ErrorConverter.toValidErrorResponseDTO(e), e.getErrorCode());
        return new ApiResponse(responseDTO);
    }
}
