#!/usr/bin/env bash
# Prueba de los ejercicios 1 y 2.
# Uso: bash curl.sh
# Requiere el servidor corriendo en localhost:8080 con los 25 turnos de data.sql.

BASE=http://localhost:8080
V1="API-Version: 1.0"
V2="API-Version: 2.0"

echo "=============================================="
echo " Ejercicio 1: paginación"
echo "=============================================="

echo
echo "--- 1.1 v1 página 0, size 5 ---"
echo "Esperado: 200, 5 elementos, pagina 0, tamanio 5, totalElementos 25, totalPaginas 5."
echo "          Cada elemento con fechaHora."
echo
curl -s -H "$V1" "$BASE/api/turnos?page=0&size=5"

echo
echo "--- 1.2 v2 página 0, size 5 ---"
echo "Esperado: 200, 5 elementos, totalElementos 25, totalPaginas 5."
echo "          Cada elemento con fecha y hora separadas, sin fechaHora."
echo
curl -s -H "$V2" "$BASE/api/turnos?page=0&size=5"

echo
echo "--- 1.3 última página parcial (page 2, size 10) ---"
echo "Esperado: 200, 5 elementos, pagina 2, totalPaginas 3."
echo
curl -s -H "$V2" "$BASE/api/turnos?page=2&size=10"

echo
echo "--- 1.4 página fuera de rango (page 99, size 10) ---"
echo "Esperado: 200, contenido vacío, totalElementos 25, totalPaginas 3."
echo
curl -s -H "$V2" "$BASE/api/turnos?page=99&size=10"

echo
echo "--- 1.5 defaults (sin page ni size) ---"
echo "Esperado: 200, pagina 0, tamanio 20, 20 elementos, totalPaginas 2."
echo
curl -s -H "$V2" "$BASE/api/turnos"

echo
echo "--- 1.6 filtro por estado CONFIRMADO ---"
echo "Esperado: 200, totalElementos 9, totalPaginas 2, todos CONFIRMADO."
echo
curl -s -H "$V2" "$BASE/api/turnos?estado=CONFIRMADO&page=0&size=5"

echo
echo "--- 1.7 filtro por cliente 'ana' ---"
echo "Esperado: 200, todos los clientes contienen 'ana' (case-insensitive)."
echo
curl -s -H "$V2" "$BASE/api/turnos?cliente=ana&page=0&size=5"

echo
echo "--- 1.8 page negativo (page -1) ---"
echo "Esperado: 400 Bad Request con errores.page."
echo
curl -s -i -H "$V2" "$BASE/api/turnos?page=-1&size=10" | head -n 20

echo
echo "--- 1.9 size fuera de rango (size 500) ---"
echo "Esperado: 400 Bad Request con errores.size."
echo
curl -s -i -H "$V2" "$BASE/api/turnos?page=0&size=500" | head -n 20

echo
echo "=============================================="
echo " Ejercicio 2: horario laboral"
echo "=============================================="

echo
echo "--- 2.1 lunes 10:30 ---"
echo "Esperado: 201 Created."
echo
curl -s -i -X POST "$BASE/api/turnos" \
  -H "Content-Type: application/json" \
  -d '{"cliente":"Test Lunes","servicio":"Corte de pelo","fechaHora":"2030-03-11T10:30:00"}' | head -n 20

echo
echo "--- 2.2 sábado 18:59 ---"
echo "Esperado: 201 Created."
echo
curl -s -i -X POST "$BASE/api/turnos" \
  -H "Content-Type: application/json" \
  -d '{"cliente":"Test Sábado","servicio":"Corte de pelo","fechaHora":"2030-03-16T18:59:00"}' | head -n 20

echo
echo "--- 2.3 límite 9:00 ---"
echo "Esperado: 201 Created (límite inferior inclusivo)."
echo
curl -s -i -X POST "$BASE/api/turnos" \
  -H "Content-Type: application/json" \
  -d '{"cliente":"Test Límite","servicio":"Corte de pelo","fechaHora":"2030-03-11T09:00:00"}' | head -n 20

echo
echo "--- 2.4 límite 19:00 ---"
echo "Esperado: 201 Created (límite superior inclusivo)."
echo
curl -s -i -X POST "$BASE/api/turnos" \
  -H "Content-Type: application/json" \
  -d '{"cliente":"Test Límite","servicio":"Corte de pelo","fechaHora":"2030-03-12T19:00:00"}' | head -n 20

echo
echo "--- 2.5 domingo 10:00 ---"
echo "Esperado: 400 Bad Request con errores.fechaHora."
echo
curl -s -i -X POST "$BASE/api/turnos" \
  -H "Content-Type: application/json" \
  -d '{"cliente":"Test Domingo","servicio":"Corte de pelo","fechaHora":"2030-03-17T10:00:00"}' | head -n 20

echo
echo "--- 2.6 lunes 8:59 ---"
echo "Esperado: 400 Bad Request con errores.fechaHora."
echo
curl -s -i -X POST "$BASE/api/turnos" \
  -H "Content-Type: application/json" \
  -d '{"cliente":"Test Antes","servicio":"Corte de pelo","fechaHora":"2030-03-11T08:59:00"}' | head -n 20

echo
echo "--- 2.7 lunes 19:01 ---"
echo "Esperado: 400 Bad Request con errores.fechaHora."
echo
curl -s -i -X POST "$BASE/api/turnos" \
  -H "Content-Type: application/json" \
  -d '{"cliente":"Test Después","servicio":"Corte de pelo","fechaHora":"2030-03-12T19:01:00"}' | head -n 20