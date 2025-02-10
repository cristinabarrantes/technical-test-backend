package com.playtomic.tests.wallet.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class MaxDecimalsValidator implements ConstraintValidator<MaxDecimals, BigDecimal> {

    private int maxDecimals;

    @Override
    public void initialize(MaxDecimals constraintAnnotation) {
        this.maxDecimals = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        int scale = value.scale();
        return scale <= maxDecimals;
    }
}
