package com.cambrian.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kuma 2020-10-16
 */
public abstract class AbstractValueSetConstraintValidator<T> implements ConstraintValidator<ValueSet, T> {

    protected Set<T> allowValues = new HashSet<>();

    protected abstract void initializeAllowValues(ValueSet constraintAnnotation);

    @Override
    public void initialize(ValueSet constraintAnnotation) {
        initializeAllowValues(constraintAnnotation);
    }

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        return allowValues.contains(value);
    }
}
