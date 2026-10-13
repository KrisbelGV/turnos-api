# Solución · Parte 3

Ejercicios resueltos en esta rama.

## Ejercicios

1. **Paginación en `GET /api/turnos`**: parámetros `page` y `size` (por defecto
   0 y 20, máximo 100), respuesta con `contenido`, `pagina`, `tamanio`,
   `totalElementos` y `totalPaginas`.
2. **`@HorarioLaboral`**: anotación propia que solo acepta turnos de lunes a
   sábado entre las 9 y las 19 hs.
3. **Documentación OpenAPI**: anotaciones `@Operation`, `@ApiResponse` y
   `@Schema` en los endpoints y DTOs para que la API sea consumible desde
   Swagger UI sin leer el código.

## Archivos nuevos

| Archivo | Qué contiene |
|---|---|
| `dto/PaginaTurnos.java` | Envoltorio genérico de paginación, con `map` para transformar el contenido. |
| `validation/HorarioLaboral.java` | Anotación de validación propia. |
| `validation/HorarioLaboralValidator.java` | Lógica de la anotación. |
| `http/curl.sh` | Prueba de los ejercicios con curl. |
| `http/turnos.http` | Prueba de los ejercicios con VS Code REST Client. |
| `http/postman.postman_collection.json` | Prueba de los ejercicios con Postman, con verificaciones automáticas. |

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `TurnoController.java` | Endpoints de listado paginados, `TurnoFiltro` como objeto de binding y documentación OpenAPI de los errores. |
| `TurnoService.java` | El método `listar` pagina y filtra sobre la lista ya filtrada. |
| `TurnoRequest.java` | Se añadió `@HorarioLaboral` sobre `fechaHora`. |
| `TurnoV2Response.java` | Anotaciones `@Schema` en cada campo. |
| `GlobalExceptionHandler.java` | Se añadió el manejo de `ConstraintViolationException`. |
| `CalendarioController.java` | Se documentó el 502 del servicio externo. |
| `data.sql` | Se amplió a 25 registros para poder probar la paginación con varias páginas. |

## Cómo correr la prueba

```bash
bash http/curl.sh
```

O importar `http/postman.postman_collection.json` en Postman y ejecutarla con
el Collection Runner. Cada request incluye las verificaciones automáticas.

La prueba asume que el servidor está corriendo en `localhost:8080` con los 25
turnos de `data.sql` cargados.