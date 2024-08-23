package com.example.scrap.validation.validator;

import com.example.scrap.entity.Scrap;
import com.example.scrap.validation.annotaion.ExistAvailableScrap;
import com.example.scrap.web.scrap.ScrapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class ExistAvailableScrapValidator implements ConstraintValidator<ExistAvailableScrap, Long> {

    private final ScrapRepository scrapRepository;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {

        if(value == null){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("필수값 입니다.").addConstraintViolation();
            return false;
        }

        // 스크랩 유효성 확인
        Optional<Scrap> scrap = scrapRepository.findById(value);
        if(scrap.isEmpty()){
            return false;
        }

        return true;
    }
}
