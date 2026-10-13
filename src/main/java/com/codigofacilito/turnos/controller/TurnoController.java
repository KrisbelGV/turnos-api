package com.codigofacilito.turnos.controller;

import com.codigofacilito.turnos.dto.TurnoRequest;
import com.codigofacilito.turnos.dto.TurnoV2Response;
import com.codigofacilito.turnos.dto.PaginaTurnos;
import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;
import com.codigofacilito.turnos.service.TurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Cambios respecto de la Parte 2:
 *  - @Valid en los @RequestBody: los errores de formato salen como ProblemDetail 400.
 *  - NUEVO (LC2): anotaciones de OpenAPI para enriquecer la documentación de Swagger UI.
 */
@Tag(name = "Turnos", description = "Reserva y gestión de turnos")
@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService service;

    public TurnoController(TurnoService service) {
        this.service = service;
    }

    // ---------- Versión 1.0 (por defecto) ----------

    @Operation(summary = "Lista turnos (v1)", description = "Devuelve cada turno con fechaHora combinada." +
            "Requiere header API-Version: 1.0")
    @ApiResponse(responseCode = "200", description = "Página de turnos")
    @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping(version = "1.0")
    public PaginaTurnos<Turno> listarV1(@Valid TurnoFiltro filtro) {
        return service.listar(filtro.estado(), filtro.cliente(), filtro.page(), filtro.size());
    }

    @Operation(summary = "Obtiene un turno (v1)")
    @ApiResponse(responseCode = "200", description = "Turno encontrado")
    @ApiResponse(responseCode = "404", description = "No existe el turno",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping(path = "/{id}", version = "1.0")
    public Turno obtenerV1(@PathVariable Long id) {
        return service.buscar(id);
    }

    // ---------- Versión 2.0: fecha y hora separadas ----------

    @Operation(summary = "Lista turnos (v2)", description = "Devuelve cada turno con fecha y hora separadas. " +
            "Requiere header API-Version: 2.0")
    @ApiResponse(responseCode = "200", description = "Página de turnos")
    @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping(version = "2.0")
    public PaginaTurnos<TurnoV2Response> listarV2(@Valid TurnoFiltro filtro) {
        return service.listar(filtro.estado(), filtro.cliente(), filtro.page(), filtro.size())
                .map(TurnoV2Response::from);
    }

    @Operation(summary = "Obtiene un turno (v2)")
    @ApiResponse(responseCode = "200", description = "Turno encontrado")
    @ApiResponse(responseCode = "404", description = "No existe el turno",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping(path = "/{id}", version = "2.0")
    public TurnoV2Response obtenerV2(@PathVariable Long id) {
        return TurnoV2Response.from(service.buscar(id));
    }

    // ---------- Sin versión: aplican a todas ----------

    @Operation(summary = "Reserva un turno nuevo")
    @ApiResponse(responseCode = "201", description = "Turno creado; el header Location apunta al recurso")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Horario ocupado",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "La fecha es feriado",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping
    public ResponseEntity<Turno> crear(@Valid @RequestBody TurnoRequest request) {
        Turno creado = service.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @Operation(summary = "Reemplaza los datos de un turno")
    @ApiResponse(responseCode = "200", description = "Turno actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "No existe el turno",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Horario ocupado",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "La fecha es feriado",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @PutMapping("/{id}")
    public Turno actualizar(@PathVariable Long id, @Valid @RequestBody TurnoRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Cancela un turno", description = "409 si ya estaba cancelado")
    @ApiResponse(responseCode = "200", description = "Turno cancelado")
    @ApiResponse(responseCode = "404", description = "No existe el turno",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "El turno ya estaba cancelado",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @PatchMapping("/{id}/cancelar")
    public Turno cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @Operation(summary = "Elimina un turno")
    @ApiResponse(responseCode = "204", description = "Turno eliminado")
    @ApiResponse(responseCode = "404", description = "No existe el turno",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Tipos auxiliares ----------

    @Schema(description = "Parámetros de paginación y filtros para listar turnos")
    public record TurnoFiltro(
            @Schema(description = "Filtrar por estado del turno", example = "PENDIENTE")
            EstadoTurno estado,

            @Schema(description = "Filtrar por nombre de cliente (contains, case-insensitive)", example = "ana")
            String cliente,

            @Schema(description = "Número de página, empezando en 0", example = "0", defaultValue = "0")
            @Min(value = 0, message = "page debe ser mayor o igual a 0")
            Integer page,

            @Schema(description = "Tamaño de página, entre 1 y 100", example = "20", defaultValue = "20")
            @Min(value = 1, message = "size debe ser al menos 1")
            @Max(value = 100, message = "size no puede superar 100")
            Integer size
    ) {
        public TurnoFiltro {
            if (page == null) page = 0;
            if (size == null) size = 20;
        }
    }

}
