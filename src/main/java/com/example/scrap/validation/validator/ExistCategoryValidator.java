package com.example.scrap.validation.validator;

import com.example.scrap.entity.Category;
import com.example.scrap.validation.annotaion.ExistCategory;
import com.example.scrap.web.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

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
        boolean notRequire = !required && value == null;
        if(notRequire){
            return true;
        }

        if(value == null){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("필수값 입니다.").addConstraintViolation();
            return false;
        }

        Optional<Category> category = categoryRepository.findById(value);
        if(category.isEmpty()){
            return false;
        }

        return true;
    }

}
