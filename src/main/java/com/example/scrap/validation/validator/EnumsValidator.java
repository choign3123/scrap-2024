package com.example.scrap.validation.validator;

import com.example.scrap.validation.annotaion.EnumsValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class EnumsValidator implements ConstraintValidator<EnumsValid, List<String>> {

    private Class<? extends Enum<?>> enumType;
    private boolean required;

    @Override
    public void initialize(EnumsValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.enumType = constraintAnnotation.enumC();
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(List<String> values, ConstraintValidatorContext context) {
        boolean nullable = !required && (values == null || values.isEmpty());
        if(nullable){
            return true;
        }

        if(values == null){
            return false;
        }

        Enum<?>[] enums = enumType.getEnumConstants();
        if(enums == null){
            return false;
        }

        // 모든 요소에 대해서 검증
        for(String value : values){
            value = value.toUpperCase();

            boolean isValid = false;
            for(Enum enumValue : enums){
                if(enumValue.name().equals(value)){
                    isValid = true; // 하나라도 일치하는게 있으면 해당 string은 valid함.
                }
            }

            if(!isValid){
                return false;
            }
        }

        return true;
    }
}
