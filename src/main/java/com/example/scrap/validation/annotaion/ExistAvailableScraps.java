package com.example.scrap.validation.annotaion;

import com.example.scrap.validation.validator.ExistAvailableScrapsValidator;

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
@Constraint(validatedBy = ExistAvailableScrapsValidator.class)
@Documented
public @interface ExistAvailableScraps {

    String message() default "해당하는 스크랩이 존재하지 않습니다.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
