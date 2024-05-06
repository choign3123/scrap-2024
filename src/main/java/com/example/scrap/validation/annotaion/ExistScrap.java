package com.example.scrap.validation.annotaion;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.validation.validator.ExistCategoryValidator;
import com.example.scrap.validation.validator.ExistScrapValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = ExistScrapValidator.class)
@Documented
public @interface ExistScrap {

    String message() default "해당하는 스크랩이 존재하지 않습니다.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
