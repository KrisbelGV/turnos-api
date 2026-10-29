package com.codigofacilito.turnos.repository;

import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos.
 * Hoy lo implementamos en memoria; en el módulo de Persistencia
 * lo reemplazamos por JdbcTemplate y después por Spring Data JPA
 * sin tocar el controller.
 */
public interface TurnoRepository {

    List<Turno> findAll();

    Optional<Turno> findById(Long id);

    List<Turno> findByEstado(EstadoTurno estado);

    Turno save(Turno turno);

    boolean existsById(Long id);

    void deleteById(Long id);
}
