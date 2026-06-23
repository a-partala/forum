package net.partala.forum.auth.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank
@Size(min = 3, max = 32)
@Pattern(
        regexp = "^[a-zA-Z0-9_.-]+$",
        message = "Only latin letters, numbers and '_', '-', '.' are allowed"
)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface UsernameValidation {

    String message() default "Invalid username format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
