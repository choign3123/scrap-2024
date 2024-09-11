package com.example.scrap.validation.annotaion;

import com.example.scrap.validation.validator.PagingSizeValidator;

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
@Constraint(validatedBy = PagingSizeValidator.class)
@Documented
public @interface PagingSize {

    String message() default "size는 1~30까지만 가능합니다.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
