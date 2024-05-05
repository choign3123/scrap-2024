package com.example.scrap.validation.validator;

import com.example.scrap.validation.annotaion.ExistCategory;
import com.example.scrap.validation.annotaion.Pagination;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PaginationValidator implements ConstraintValidator<Pagination, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value > 0;
    }
}
