package com.example.scrap.converter;

import com.example.scrap.base.exception.ValidErrorResponseDTO;
import org.springframework.validation.BindingResult;

import java.util.Optional;

public class ErrorConverter {

    public static ValidErrorResponseDTO toValidErrorResponseDTO(BindingResult bindingResult){
        String field = bindingResult.getFieldError().getField();
        String cause = Optional.ofNullable(bindingResult.getFieldError().getDefaultMessage()).orElse("잘못된 요청입니다.");

        return new ValidErrorResponseDTO(field, cause);
    }
}
