package com.codigofacilito.turnos.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * La lógica de @HorarioLaboral. Hibernate Validator llama a isValid()
 * con el valor del campo anotado.
 */
public class HorarioLaboralValidator implements ConstraintValidator<HorarioLaboral, LocalDateTime> {

    private static final LocalTime APERTURA = LocalTime.of(9, 0);
    private static final LocalTime CIERRE = LocalTime.of(19, 0);

    @Override
    public boolean isValid(LocalDateTime valor, ConstraintValidatorContext context) {
        if (valor == null) {
            return true; // de los null se encarga @NotNull
        }
        boolean diaHabil = valor.getDayOfWeek() != DayOfWeek.SUNDAY;
        LocalTime hora = valor.toLocalTime();
        boolean enHorario = !hora.isBefore(APERTURA) && hora.isBefore(CIERRE);
        return diaHabil && enHorario;
    }
}
