package by.testtask.balancehub.utils.validators;

import by.testtask.balancehub.utils.validators.impl.PositiveBalanceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PositiveBalanceValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveBalance {
    String message() default "Balance must be positive";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
