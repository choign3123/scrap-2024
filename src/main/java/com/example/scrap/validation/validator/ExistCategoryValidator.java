package com.example.scrap.validation.validator;

import com.example.scrap.validation.annotaion.ExistCategory;
import com.example.scrap.web.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
@Slf4j
public class ExistCategoryValidator implements ConstraintValidator<ExistCategory, Long> {

    private final CategoryRepository categoryRepository;
    private boolean required;

    @Override
    public void initialize(ExistCategory constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {

        boolean nullable = !required && value == null;
        if(nullable){
            return true;
        }

        if(value == null || !categoryRepository.existsById(value)){
            return false;
        }

        return true;
    }

}
