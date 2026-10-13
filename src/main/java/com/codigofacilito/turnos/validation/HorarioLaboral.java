package com.codigofacilito.turnos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Aceota turnos de lunes a sábado, entre las 9:00 y las 19:00 hs.
 * Los limites son inclusivos en ambos extremos: 9:00 y 19:00 son válidos.
 * */
@Documented
@Constraint(validatedBy = HorarioLaboralValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface HorarioLaboral {

    String message() default "El turno debe ser de lunes a sábado entre las 9 y las 19h";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
