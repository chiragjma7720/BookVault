package com.solvative.bookvault.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = IsbnValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ValidIsbn {
	String message() default "ISBN must match format 978-XXXXXXXXXX";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

