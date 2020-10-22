package com.cambrian.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author kuma 2020-10-16
 */
@Documented
@Constraint(validatedBy = { ValueSetConstraintValidatorForInteger.class, ValueSetConstraintValidatorForString.class})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface ValueSet {

    String message() default "{com.cambrian.validation.constraints.ValueSet.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    int[] intValues() default { };

    String[] stringValues() default { };
}
