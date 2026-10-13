package com.codigofacilito.turnos.dto;

import com.codigofacilito.turnos.model.Turno;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.function.Function;
import java.util.List;

@Schema(description = "Página de resultados")
public record PaginaTurnos<T> (
        @Schema(description = "Elementos de esta página")
        List<T> contenido,

        @Schema(description = "Número de página actual, empezando en 0", example = "0")
        int pagina,

        @Schema(description = "Tamaño de página solicitado (no la cantidad efectiva de la última página)", example = "20")
        int tamanio,

        @Schema(description = "Total de elementos que matchean los filtros aplicados", example = "25")
        long totalElementos,

        @Schema(description = "Total de páginas, calculado como ceil(totalElementos/tamanio)", example = "2")
        int totalPaginas
) {
    public <R> PaginaTurnos<R> map(Function<T, R> transformador) {
        return new PaginaTurnos<>(
                contenido.stream().map(transformador).toList(),
                pagina,
                tamanio,
                totalElementos,
                totalPaginas);
    }
}
