package com.cambrian.common.valid;

/**
 * @author kuma 2020-10-16
 */
public class ValueSetConstraintValidatorForInteger extends AbstractValueSetConstraintValidator<Integer> {

    @Override
    protected void initializeAllowValues(ValueSet constraintAnnotation) {
        for (int i : constraintAnnotation.intValues()) {
            allowValues.add(i);
        }
    }
}
