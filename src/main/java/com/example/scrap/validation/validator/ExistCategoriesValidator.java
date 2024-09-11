package com.example.scrap.validation.validator;

import com.example.scrap.entity.Category;
import com.example.scrap.validation.annotaion.ExistCategories;
import com.example.scrap.web.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class ExistCategoriesValidator implements ConstraintValidator<ExistCategories, List<Long>> {

    private final CategoryRepository categoryRepository;
    private boolean required;

    @Override
    public void initialize(ExistCategories constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(List<Long> value, ConstraintValidatorContext context) {

        boolean notRequire = !required && (value == null || value.isEmpty());
        if(notRequire){
            return true;
        }

        if(value == null || value.isEmpty()){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("필수값 입니다.").addConstraintViolation();
            return false;
        }

        for(Long categoryId : value){
            if(categoryId == null){
                return false;
            }

            Optional<Category> category = categoryRepository.findById(categoryId);
            if(category.isEmpty()){
                return false;
            }
        }

        return true;
    }
}
