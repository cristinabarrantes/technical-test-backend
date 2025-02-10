package com.playtomic.tests.wallet.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MaxDecimalsValidator.class)
public @interface MaxDecimals {

	String message() default "must have at maximum 2 decimals";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int value();
}
