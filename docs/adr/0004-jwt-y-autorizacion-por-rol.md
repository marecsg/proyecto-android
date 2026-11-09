# ADR-004: JWT y autorización por rol en la API

- **Estado:** Implementación parcial; contrato y restricciones de rol pendientes de corrección/verificación.
- **Fecha:** 2026-11-09.
- **Relaciones:** RF-01, RF-05, R5, SC-03, CP-08 (propuesto).

## Contexto
El Charter define Estudiante y Encargado. El backend publicado usa Spring Security, un filtro JWT y BCrypt. En `SecurityConfig.java` hay autenticación general de peticiones, pero la configuración examinada no demuestra autorización por rol para aprobar/rechazar. En el cliente se observan impresiones de tokens en depuración; el backend imprime información sensible del hash de contraseña durante el login. Además, el contrato `AuthResponse` del cliente requiere un objeto `user`, mientras el `AuthController` publicado responde un mapa plano con `nombres`, `correo` y `rol`.

## Decisión
Conservar autenticación tokenizada en API y aplicar validación del rol autorizado **en servidor** para operaciones de encargado, además del control visual en la app. Estabilizar un contrato de login versionado (DTOs compatibles), retirar los logs de tokens y hashes, y externalizar los secretos/configuración de base de datos. No afirmar que este comportamiento ya está probado.

## Alternativas
1. Ocultar botones en la app: insuficiente, la API puede invocarse directamente.
2. Sesiones en servidor: introduce estado y administración adicional.
3. Integrar OIDC institucional: deseable futuro, no confirmado para el MVP.

## Consecuencias
- **+** API protegida incluso si se modifica el cliente.
- **+** Separación de permisos y trazabilidad de decisiones del encargado.
- **-** Requiere gestionar expiración de tokens, fallos 401/403 y secretos.
- **-** Hay que coordinar y probar el contrato con el backend actualizado.

## Verificación
CP-08: estudiante autenticado llama a `PUT /api/solicitudes/{id}/responder` y recibe HTTP 403; encargado autorizado puede responder, con auditoría. Prueba pendiente.
