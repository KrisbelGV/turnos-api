package com.codigofacilito.turnos.repository;

import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * LC2: el mismo repositorio con JdbcTemplate.
 * JdbcTemplate se encarga de todo lo repetitivo del LC1:
 * pedir y cerrar la conexión, preparar la sentencia, recorrer el ResultSet
 * y convertir las SQLException en excepciones de Spring (DataAccessException, unchecked).
 * Nosotros solo escribimos el SQL y cómo mapear una fila.
 */
@Repository
public class JdbcTemplateTurnoRepository implements TurnoRepository {

    private static final String COLUMNAS = "id, cliente, servicio, fecha_hora, estado";

    // NUEVO: el mapeo de una fila a un Turno se escribe UNA vez y se reutiliza
    private static final RowMapper<Turno> TURNO_MAPPER = (rs, numeroDeFila) -> new Turno(
            rs.getLong("id"),
            rs.getString("cliente"),
            rs.getString("servicio"),
            rs.getObject("fecha_hora", LocalDateTime.class),
            EstadoTurno.valueOf(rs.getString("estado")));

    // JdbcTemplate lo crea Spring Boot a partir del DataSource
    private final JdbcTemplate jdbc;

    public JdbcTemplateTurnoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Turno> findAll() {
        return jdbc.query("SELECT " + COLUMNAS + " FROM turno", TURNO_MAPPER);
    }

    @Override
    public Optional<Turno> findById(Long id) {
        // query() devuelve una lista: vacía si no hay filas.
        // (queryForObject lanzaría EmptyResultDataAccessException si no encuentra nada)
        return jdbc.query("SELECT " + COLUMNAS + " FROM turno WHERE id = ?", TURNO_MAPPER, id)
                .stream()
                .findFirst();
    }

    @Override
    public Turno save(Turno turno) {
        if (turno.id() == null) {
            // Para recuperar el id generado todavía hay que armar el PreparedStatement
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO turno (cliente, servicio, fecha_hora, estado) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, turno.cliente());
                ps.setString(2, turno.servicio());
                ps.setObject(3, turno.fechaHora());
                ps.setString(4, turno.estado().name());
                return ps;
            }, keyHolder);
            return turno.conId(keyHolder.getKey().longValue());
        }
        // Para el resto alcanza con el SQL y los parámetros, en orden
        jdbc.update("UPDATE turno SET cliente = ?, servicio = ?, fecha_hora = ?, estado = ? WHERE id = ?",
                turno.cliente(), turno.servicio(), turno.fechaHora(), turno.estado().name(), turno.id());
        return turno;
    }

    @Override
    public boolean existsById(Long id) {
        Integer cantidad = jdbc.queryForObject("SELECT COUNT(*) FROM turno WHERE id = ?", Integer.class, id);
        return cantidad != null && cantidad > 0;
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM turno WHERE id = ?", id);
    }
}
