package com.codigofacilito.turnos.repository;

import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import java.util.Map;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * LC2: el mismo repositorio con JdbcTemplate, ahora con NamedParameterJdbcTemplate.
 * A diferencia con ? los parámetros van con :nombre y en un Map, así que no importa
 * el orden y un valor repetido se pasa una sola vez. Por debajo Spring sigue armando
 * un PreparedStatement con ?, no cambia el motor ni el rendimiento: es legibilidad.
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
    private final NamedParameterJdbcTemplate jdbc;
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateTurnoRepository(NamedParameterJdbcTemplate jdbc, JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbc;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Turno> findAll() {
        // Sin parámetros, la versión con :nombre es idéntica a la de ?
        // no hay nada que nombrar. La comparación se ve en findById/findByEstado.
        return jdbc.query("SELECT " + COLUMNAS + " FROM turno", TURNO_MAPPER);
    }

    @Override
    public Optional<Turno> findById(Long id) {
        // query() devuelve una lista: vacía si no hay filas.
        // (queryForObject lanzaría EmptyResultDataAccessException si no encuentra nada)
        return jdbc.query("SELECT " + COLUMNAS + " FROM turno WHERE id = :id", Map.of("id", id), TURNO_MAPPER)
                .stream()
                .findFirst();
    }

    @Override
    public List<Turno> findByEstado(EstadoTurno estado) {
        return jdbc.query("SELECT " + COLUMNAS + " FROM turno WHERE estado = :estado", Map.of("estado", estado.name()), TURNO_MAPPER);
    }

    @Override
    public Turno save(Turno turno) {
        if (turno.id() == null) {
            // INSERT: necesita recuperar el id generado, así que armamos el
            // PreparedStatement a mano y por eso vuelve a ? (JDBC puro).
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
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
    
        // UPDATE: acá sí aplica :nombre, y el id va una sola vez en el Map.
        // Comparado con la versión con ? : no hay que cuidar el orden de los argumentos.
        jdbc.update(
            "UPDATE turno SET cliente = :cliente, servicio = :servicio, " +
            "fecha_hora = :fechaHora, estado = :estado WHERE id = :id",
            Map.of("cliente", turno.cliente(),
                   "servicio", turno.servicio(),
                   "fechaHora", turno.fechaHora(),
                   "estado", turno.estado().name(),
                   "id", turno.id()));
        return turno;
    }

    @Override
    public boolean existsById(Long id) {
        Integer cantidad = jdbc.queryForObject("SELECT COUNT(*) FROM turno WHERE id = :id", Map.of("id", id), Integer.class);
        return cantidad != null && cantidad > 0;
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM turno WHERE id = :id", Map.of("id", id));
    }
}
