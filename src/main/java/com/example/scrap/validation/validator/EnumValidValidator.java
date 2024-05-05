package com.example.scrap.validation.validator;

import com.example.scrap.validation.annotaion.EnumValid;
import com.example.scrap.validation.annotaion.PagingSize;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValidValidator implements ConstraintValidator<EnumValid, String> {

    private Enum<?>[] enums;

    @Override
    public void initialize(EnumValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        enums = constraintAnnotation.enumC().getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        value = value.toUpperCase();

        if(enums == null){
            return false;
        }

        for(Enum enumValue : enums){
            if(enumValue.name().equals(value)){
                return true;
            }
        }

        return false;
    }
}
