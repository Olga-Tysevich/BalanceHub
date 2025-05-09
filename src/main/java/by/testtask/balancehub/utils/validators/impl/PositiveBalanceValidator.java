package by.testtask.balancehub.utils.validators.impl;

import by.testtask.balancehub.utils.validators.PositiveBalance;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class PositiveBalanceValidator implements ConstraintValidator<PositiveBalance, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

}

