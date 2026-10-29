# Solución · Persistencia Parte 1

Ejercicios resueltos en esta rama.

## Ejercicios

1. **`findByEstado` en el repositorio**: nuevo método `List<Turno> findByEstado(EstadoTurno estado)`
   en `TurnoRepository`, implementado en `JdbcTemplateTurnoRepository` con `WHERE estado = :estado`.
   `TurnoService.listar` ya no filtra por estado en memoria: delega en el repositorio y solo
   filtra por `cliente` en Java.
2. **`NamedParameterJdbcTemplate`**: `JdbcTemplateTurnoRepository` migró a parámetros con nombre
   (`:id`, `:estado`, …) en vez de `?`. El `INSERT` con `GeneratedKeyHolder` es la única excepción que
   sigue usando `?` al requerir una lambda `PreparedStatementCreator` para recuperar el ID generado,
   se utiliza la inyección del JdbcTemplate clásico
3. **Para pensar — tabla `cliente` con CRUD propio**: con JdbcTemplate cada tabla suma 1 interface
   + 1 implementación + ~6 queries a mano y su `RowMapper`. Con Spring Data JPA se reduce a una
   interface que extiende `JpaRepository` más consultas derivadas. Esa diferencia motiva la
   migración de la Parte 2.

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `TurnoRepository.java` | Nuevo método `findByEstado(EstadoTurno estado)` e import de `EstadoTurno`. |
| `JdbcTemplateTurnoRepository.java` | Migración a `NamedParameterJdbcTemplate` (`:nombre` + `Map`); implementación de `findByEstado` con `WHERE`. |
| `TurnoService.java` | `listar` delega el filtro por estado en el repositorio y solo filtra por `cliente` en Java. |

## Notas

- **`estado = null` en SQL** se evalúa como `unknown` y no matchea, por eso `listar` ramifica:
  `estado == null` → `findAll()`, si no → `findByEstado(estado)`.
- **El filtro por `cliente` sigue en Java** porque el ejercicio solo pide mover el estado al SQL.
  Combinar ambos habría requerido además un `findByEstadoAndCliente`; esa explosión de métodos
  es lo que la Parte 2 resuelve con consultas derivadas.
- **`findAll()` no tiene parámetros**, así que es idéntico con `?` o con `:nombre`: la comparación
  entre ambos estilos se ve en `findById`, `findByEstado`, `existsById`, `deleteById` y el `UPDATE`.

## Cómo compilar y probar

```bash
mvn test
mvn spring-boot:run
```