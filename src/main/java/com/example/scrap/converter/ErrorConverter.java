package com.example.scrap.converter;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.base.exception.ValidErrorResponseDTO;
import com.example.scrap.base.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;

import javax.validation.ConstraintViolation;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class ErrorConverter {

    public static ValidErrorResponseDTO toValidErrorResponseDTO(BindingResult bindingResult){
        String field = bindingResult.getFieldError().getField();
        String cause = Optional.ofNullable(bindingResult.getFieldError().getDefaultMessage()).orElse("잘못된 요청입니다.");

        return new ValidErrorResponseDTO(field, cause);
    }

    public static ValidErrorResponseDTO toValidErrorResponseDTO(Set<ConstraintViolation<?>> cvSet){
        ConstraintViolation<?> cv = cvSet.stream().findFirst()
                .orElseThrow(() -> new BaseException(ErrorCode._INTERNAL_SERVER_ERROR));

        String[] fieldList = cv.getPropertyPath().toString().split("\\.");
        String field = fieldList[fieldList.length - 1];
        String cause = cv.getMessage();

        return new ValidErrorResponseDTO(field, cause);
    }

    public static ValidErrorResponseDTO toValidErrorResponseDTO(ValidationException e){

        return new ValidErrorResponseDTO(e.getFiled(), e.getCauses());
    }
}
