package com.example.scrap.validation.annotaion;

import com.example.scrap.validation.validator.ExistCategoriesValidator;
import com.example.scrap.validation.validator.ExistCategoryValidator;

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
@Constraint(validatedBy = ExistCategoriesValidator.class)
@Documented
public @interface ExistCategories {

    String message() default "해당하는 카테고리가 존재하지 않습니다.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    boolean required() default true;

}
