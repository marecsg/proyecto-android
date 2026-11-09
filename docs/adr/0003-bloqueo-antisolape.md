# ADR-003: Garantizar que no haya préstamos superpuestos

- **Estado:** Propuesta crítica; requiere inspección del backend actualizado y prueba concurrente.
- **Fecha:** 2026-11-09.
- **Relaciones:** RF-04, RN-01, REQ-11 (Matriz_Entregable), R1, SC-01, CP-05.

## Contexto
Dos encargados podrían aprobar solicitudes del mismo equipo y horario simultáneamente. El backlog propone un `trigger` anti-solape en MySQL (historia 017, en progreso). El Project Charter exige cero préstamos aceptados o en uso que se solapen. El código publicado no acredita todavía un `gle.sql` versionado ni un test concurrente que cumpla esta condición.

## Decisión propuesta
Centralizar la validación de superposición en el backend/BD **dentro de una transacción**, con un mecanismo de serialización por equipo (bloqueo de la fila de equipo mediante `SELECT ... FOR UPDATE` o estrategia equivalente), revalidación de intervalos y estados incompatibles antes del `COMMIT`, e índice por equipo/tiempos. Si el equipo mantiene un trigger MySQL, verificar su comportamiento concurrente en la misma transacción; **un trigger de consulta por sí solo no prueba ausencia de carreras**. Devolver HTTP `409 Conflict` al detectar superposición y registrar la decisión. La técnica exacta se aprobará tras revisar el código actualizado.

## Alternativas
1. Validar solo desde la app: se descarta por concurrencia y clientes múltiples.
2. Consulta sin bloqueo en el backend: se descarta por condiciones de carrera.
3. Redis lock: requiere nueva infraestructura y control de expiración.

## Consecuencias
- **+** Regla de negocio aplicada a cualquier cliente.
- **+** Comportamiento verificable mediante solicitudes simultáneas.
- **-** Puede producir contención y necesitar reintentos/control de deadlocks.
- **-** La solución exacta depende de la versión de MySQL y del modelo transaccional real.

## Verificación
CP-05: lanzar 100 intentos concurrentes para el mismo equipo/intervalo; deben existir **a lo sumo 1 préstamo aceptado** y los demás recibir conflicto/estado rechazado; no debe haber dobles préstamos ni inconsistencias en BD. Prueba pendiente.
