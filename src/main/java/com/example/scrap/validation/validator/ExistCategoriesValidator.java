package com.example.scrap.validation.validator;

import com.example.scrap.validation.annotaion.ExistCategories;
import com.example.scrap.validation.annotaion.ExistCategory;
import com.example.scrap.web.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ExistCategoriesValidator implements ConstraintValidator<ExistCategories, List<Long>> {

    private final CategoryRepository categoryRepository;

    @Override
    public boolean isValid(List<Long> value, ConstraintValidatorContext context) {

        for(Long categoryId : value){
            if(categoryId == null || !categoryRepository.existsById(categoryId)){
                return false;
            }
        }

        return true;
    }
}
