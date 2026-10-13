# turnos-api

API de reserva de turnos con Spring Boot 4, H2 y documentación OpenAPI.

## Origen

Este repositorio es un fork de [tatianaborda/turnos-api](https://github.com/tatianaborda/turnos-api),
el proyecto del Bootcamp Backend con Java y Spring (Código Facilito).

Mi solución personal de los ejercicios de este repositorio vive en ramas
propias. La rama `main` sigue el material del curso y puede ser reemplazada
por la solución oficial en cualquier momento, por lo que cada solución se
conserva en su rama.

- Repositorio original: https://github.com/tatianaborda/turnos-api
- Mis notas del bootcamp: https://github.com/KrisbelGV/notas-java-spring-codigo-facilito
- Mi perfil: https://github.com/krisbelgv

## Ramas

| Rama | Contenido |
|---|---|
| `main` | Material del curso, actualizado con cada clase. |
| `solucion-parte-3` | Solución personal de los ejercicios de la Parte 3. Ver `SOLUCION.md` en esa rama. |

Las ramas de solución no se modifican una vez creadas. Si `main` cambia, la
rama sigue intacta.

## Cómo levantar el proyecto

```bash
mvn spring-boot:run
```

El servidor queda en `http://localhost:8080`.

Para ver la API de forma interactiva, abrir Swagger UI en
`http://localhost:8080/swagger-ui.html`. La consola H2 está disponible en
`http://localhost:8080/h2-console` (URL `jdbc:h2:mem:turnos`, usuario `sa`,
sin password).

## Cómo usar una solución

```bash
git clone https://github.com/krisbelgv/turnos-api.git
cd turnos-api
git checkout solucion-parte-3   # reemplazar por la rama que quieras ver
```

Si `main` ya fue reemplazado por la solución oficial, la rama de la parte que
buscas sigue disponible.

---

Este README es la base común a todas las ramas. Lo específico de cada solución
está en `SOLUCION.md` dentro de la rama correspondiente.