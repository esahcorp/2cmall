package com.cambrian.common.valid;

import java.util.Arrays;

/**
 * @author kuma 2020-10-16
 */
public class ValueSetConstraintValidatorForString extends AbstractValueSetConstraintValidator<String> {

    @Override
    protected void initializeAllowValues(ValueSet constraintAnnotation) {
        allowValues.addAll(Arrays.asList(constraintAnnotation.stringValues()));
    }
}
