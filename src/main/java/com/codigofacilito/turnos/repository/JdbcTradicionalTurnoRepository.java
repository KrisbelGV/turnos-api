package com.codigofacilito.turnos.repository;

import com.codigofacilito.turnos.model.EstadoTurno;
import com.codigofacilito.turnos.model.Turno;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * LC1: el mismo repositorio, escrito con JDBC "a mano".
 * Así se accedía a una base de datos antes de Spring (y así trabaja JDBC por debajo).
 *
 * Fijate en lo que se repite en TODOS los métodos:
 *  1. pedir una conexión          -> dataSource.getConnection()
 *  2. preparar la sentencia SQL   -> prepareStatement(sql)
 *  3. cargar los parámetros       -> setString / setLong / setObject (por posición: 1, 2, 3...)
 *  4. ejecutar                    -> executeQuery() o executeUpdate()
 *  5. recorrer el resultado       -> while (rs.next())
 *  6. cerrar todo                 -> try-with-resources
 *  7. manejar SQLException        -> obligatoria (checked)
 * Solo el SQL y el mapeo cambian de un método a otro. El resto es boilerplate.
 */
@Repository
public class JdbcTradicionalTurnoRepository implements TurnoRepository {

    private static final String COLUMNAS = "id, cliente, servicio, fecha_hora, estado";

    // El DataSource lo configura Spring Boot con las properties spring.datasource.*
    private final DataSource dataSource;

    public JdbcTradicionalTurnoRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Turno> findAll() {
        String sql = "SELECT " + COLUMNAS + " FROM turno";
        List<Turno> turnos = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                turnos.add(mapear(rs));
            }
            return turnos;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar los turnos", e);
        }
    }

    @Override
    public Optional<Turno> findById(Long id) {
        String sql = "SELECT " + COLUMNAS + " FROM turno WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id); // el primer "?" del SQL
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al buscar el turno " + id, e);
        }
    }

    @Override
    public Turno save(Turno turno) {
        return turno.id() == null ? insertar(turno) : actualizar(turno);
    }

    private Turno insertar(Turno turno) {
        String sql = "INSERT INTO turno (cliente, servicio, fecha_hora, estado) VALUES (?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             // RETURN_GENERATED_KEYS: queremos que la base nos devuelva el id que generó
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, turno.cliente());
            ps.setString(2, turno.servicio());
            ps.setObject(3, turno.fechaHora());
            ps.setString(4, turno.estado().name());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                claves.next();
                return turno.conId(claves.getLong(1));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar el turno", e);
        }
    }

    private Turno actualizar(Turno turno) {
        String sql = "UPDATE turno SET cliente = ?, servicio = ?, fecha_hora = ?, estado = ? WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, turno.cliente());
            ps.setString(2, turno.servicio());
            ps.setObject(3, turno.fechaHora());
            ps.setString(4, turno.estado().name());
            ps.setLong(5, turno.id());
            ps.executeUpdate();
            return turno;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar el turno " + turno.id(), e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM turno WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al verificar el turno " + id, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM turno WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar el turno " + id, e);
        }
    }

    // Convierte la fila actual del ResultSet en un Turno
    private Turno mapear(ResultSet rs) throws SQLException {
        return new Turno(
                rs.getLong("id"),
                rs.getString("cliente"),
                rs.getString("servicio"),
                rs.getObject("fecha_hora", LocalDateTime.class),
                EstadoTurno.valueOf(rs.getString("estado")));
    }
}
