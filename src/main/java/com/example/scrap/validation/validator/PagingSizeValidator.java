package com.example.scrap.validation.validator;

import com.example.scrap.validation.annotaion.PagingSize;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PagingSizeValidator implements ConstraintValidator<PagingSize, Integer> {

    // TODO: page는 0 이상부터 가능함!! 30 넘어간다고 안되지 않음!
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return 0 < value && value <= 30;
    }
}
