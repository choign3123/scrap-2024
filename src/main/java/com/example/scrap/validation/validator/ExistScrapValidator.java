package com.example.scrap.validation.validator;

import com.example.scrap.validation.annotaion.ExistCategory;
import com.example.scrap.validation.annotaion.ExistScrap;
import com.example.scrap.web.category.CategoryRepository;
import com.example.scrap.web.scrap.ScrapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
@Slf4j
public class ExistScrapValidator implements ConstraintValidator<ExistScrap, Long> {

    private final ScrapRepository scrapRepository;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {

        if(value == null || !scrapRepository.existsById(value)){
            return false;
        }

        return true;
    }
}
