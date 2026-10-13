package com.codigofacilito.turnos.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class HorarioLaboralValidator implements ConstraintValidator<HorarioLaboral, LocalDateTime> {

    private static final LocalTime APERTURA = LocalTime.of(9, 0);
    private static final LocalTime CIERRE = LocalTime.of(19, 0);

    @Override
    public boolean isValid(LocalDateTime fechaHora, ConstraintValidatorContext context) {
        // nul se delega a @NotNull
        if (fechaHora == null) {
            return true;
        }
        DayOfWeek dia = fechaHora.getDayOfWeek();
        if (dia == DayOfWeek.SUNDAY) {
            return false;
        }
        LocalTime hora = fechaHora.toLocalTime();
        return !hora.isBefore(APERTURA) && !hora.isAfter(CIERRE);
    }
}
