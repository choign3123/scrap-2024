package com.example.scrap.validation.validator;

import com.example.scrap.entity.Scrap;
import com.example.scrap.validation.annotaion.ExistAvailableScraps;
import com.example.scrap.web.scrap.ScrapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ExistAvailableScrapsValidator implements ConstraintValidator<ExistAvailableScraps, List<Long>> {

    private final ScrapRepository scrapRepository;

    @Override
    public boolean isValid(List<Long> value, ConstraintValidatorContext context) {

        for(Long scrapId : value){
            if(scrapId == null || !scrapRepository.existsById(scrapId)){
                return false;
            }

            // 스크랩 유효성 확인
            Scrap scrap = scrapRepository.findById(scrapId).get();
            if(!scrap.isAvailable()){
                return false;
            }
        }

        return true;
    }
}
