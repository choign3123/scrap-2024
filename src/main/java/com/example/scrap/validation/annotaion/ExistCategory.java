package com.example.scrap.validation.annotaion;

import com.example.scrap.validation.validator.ExistCategoryValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = ExistCategoryValidator.class)
@Documented
public @interface ExistCategory {

    String message() default "해당하는 카테고리가 존재하지 않습니다.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
