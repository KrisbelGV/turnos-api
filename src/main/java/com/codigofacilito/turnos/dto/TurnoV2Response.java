package com.codigofacilito.turnos.dto;

import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Versión 2 del contrato: el front pidió fecha y hora por separado.
 * Es un cambio incompatible, por eso va en una versión nueva de la API.
 */
public record TurnoV2Response(

        @Schema(description = "Identificador del turno", example = "1")
        Long id,

        @Schema(description = "Nombre del cliente", example = "Ana Pérez")
        String cliente,

        @Schema(description = "Servicio reservado", example = "Corte de pelo")
        String servicio,

        @Schema(description = "Fecha del turno", example = "2030-03-04")
        LocalDate fecha,

        @Schema(description = "Hora del turno", example = "10:30:00")
        LocalTime hora,

        @Schema(description = "Estado actual del turno", example = "PENDIENTE")
        EstadoTurno estado
) {
    public static TurnoV2Response from(Turno turno) {
        return new TurnoV2Response(turno.id(), turno.cliente(), turno.servicio(),
                turno.fechaHora().toLocalDate(), turno.fechaHora().toLocalTime(), turno.estado());
    }
}
