package com.example.scrap.validation.validator;

import com.example.scrap.entity.Scrap;
import com.example.scrap.validation.annotaion.ExistAvailableScrap;
import com.example.scrap.web.scrap.ScrapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
@Slf4j
public class ExistAvailableScrapValidator implements ConstraintValidator<ExistAvailableScrap, Long> {

    private final ScrapRepository scrapRepository;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {

        if(value == null){
            return false;
        }

        // 스크랩 유효성 확인
        Scrap scrap = scrapRepository.findById(value).orElse(null);
        if(scrap == null || !scrap.isAvailable()){
            return false;
        }

        return true;
    }
}
