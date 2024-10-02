package com.example.scrap.validation.validator;

import com.example.scrap.validation.annotaion.PagingSize;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PagingSizeValidator implements ConstraintValidator<PagingSize, Integer> {


    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return 0 < value && value <= 30;
    }
}
