package com.stellarstay.hotelsystem.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReservationDatesValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReservationDates {
    String message() default "checkOutDate must be after checkInDate.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
