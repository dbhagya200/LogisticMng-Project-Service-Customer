package lk.ijse.eca.customerservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidImageValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {
    String message() default "Only image files are allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
