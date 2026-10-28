# turnos-api · Persistencia, Parte 1

Proyecto del Bootcamp Backend con Java y Spring (Código Facilito).
Estado al final de **Persistencia de Datos · Parte 1 (27/10/2026)**: el acceso a datos con JDBC tradicional y con JdbcTemplate, sobre una base H2 que guarda los datos en un archivo, con el pool de conexiones HikariCP configurado.

## Novedades respecto de la clase 3
- **Solución del ejercicio de la clase 3:** `@HorarioLaboral`, una anotación de validación propia (lunes a sábado, de 9 a 19 hs).
- **Sin repositorio en memoria:** la API trabaja siempre contra la base de datos.
- **JDBC tradicional (LC1):** el repositorio escrito con `Connection`, `PreparedStatement` y `ResultSet`, para ver el boilerplate.
- **JdbcTemplate (LC2):** el mismo repositorio, con Spring encargándose de abrir y cerrar conexiones, recorrer resultados y traducir excepciones.
- **DataSource y HikariCP (LC3):** H2 en modo archivo (`./data/turnos`), los datos sobreviven a los reinicios, y el pool de conexiones configurado y visible en el log.

## Ejecutar
```bash
mvn spring-boot:run
mvn test        # los tests usan una base en memoria, no tocan ./data
```

| Qué | Dónde |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Consola H2 | http://localhost:8080/h2-console (URL `jdbc:h2:file:./data/turnos`, usuario `sa`, sin password) |

Para empezar con la base vacía de nuevo: frená la app y borrá la carpeta `data/`.

## Ejercicio para la casa
1. Agregar a `TurnoRepository` un método `List<Turno> findByEstado(EstadoTurno estado)` e implementarlo en `JdbcTemplateTurnoRepository` con un `WHERE` en el SQL. Usarlo en `TurnoService.listar` en vez de filtrar en memoria.
2. Escribir `JdbcTemplateTurnoRepository.findAll()` con `NamedParameterJdbcTemplate` (parámetros con nombre, `:id`) y comparar con los `?`.
3. Para pensar: ¿cuántas líneas tendría que escribir para agregar una tabla `cliente` con su propio CRUD? El jueves lo resolvemos con Spring Data JPA.

## Cómo sigue
- Persistencia 2 (29/10): Spring Data JPA, entidades, repositorios, consultas derivadas, `@Query` y paginación con `Pageable`.
