package com.codigofacilito.turnos.controller;

import com.codigofacilito.turnos.client.Feriado;
import com.codigofacilito.turnos.service.CalendarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.List;

@Tag(name = "Calendario", description = "Feriados del país configurado")
@RestController
@RequestMapping("/api/calendario")
public class CalendarioController {

    private final CalendarioService calendario;

    public CalendarioController(CalendarioService calendario) {
        this.calendario = calendario;
    }

    @Operation(summary = "Lista los feriados de un año (por defecto, el actual)")
    @ApiResponse(responseCode = "200", description = "Lista de feriados del año solicitado")
    @ApiResponse(responseCode = "502", description = "El servicio externo de feriados no está disponible")
    @GetMapping("/feriados")
    public List<Feriado> feriados(@RequestParam(required = false) Integer anio) {
        return calendario.feriadosDe(anio != null ? anio : Year.now().getValue());
    }
}
