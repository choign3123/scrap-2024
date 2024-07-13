package com.example.scrap.validation.validator;

import com.example.scrap.validation.annotaion.PagingPage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PagingPageValidator implements ConstraintValidator<PagingPage, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value >= 0;
    }
}
