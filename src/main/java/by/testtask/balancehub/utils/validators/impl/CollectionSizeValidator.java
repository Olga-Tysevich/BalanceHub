package by.testtask.balancehub.utils.validators.impl;

import by.testtask.balancehub.utils.validators.CollectionSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.Objects;

public class CollectionSizeValidator implements ConstraintValidator<CollectionSize, Collection<?>> {

    private int min;
    private int max;

    @Override
    public void initialize(CollectionSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }
        return value.size() >= min && value.size() <= max;
    }
}
