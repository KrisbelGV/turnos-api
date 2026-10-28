package com.codigofacilito.turnos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Solución del ejercicio 2 de la clase 3: una anotación de validación propia.
 * Solo acepta turnos de lunes a sábado, de 9 a 19 hs.
 *
 * Toda anotación de Bean Validation necesita:
 *  - @Constraint: qué clase hace la validación
 *  - message, groups y payload: los tres atributos que exige el estándar
 */
@Documented
@Constraint(validatedBy = HorarioLaboralValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface HorarioLaboral {

    String message() default "Los turnos son de lunes a sábado, de 9 a 19 hs";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
