# ADR-002: Lectura QR en la aplicación móvil

- **Estado:** Propuesta, NO implementada en el snapshot revisado.
- **Fecha:** 2026-11-09.
- **Relaciones:** RF-02; R2; SC-02; CP-03 y CP-07 (propuesto).

## Contexto
El Charter exige identificar equipos mediante un código QR físico. El backlog del equipo indica uso previsto de CameraX y la visualización de un BottomSheet; las historias 010 y 011 aparecen en progreso. En `App.kt` de la versión revisada, el evento `onScanQr` muestra un mensaje indicando que el escáner aún no está implementado.

## Decisión propuesta
Integrar la lectura QR por cámara en Android mediante CameraX y un decodificador compatible, extraer **solo un ID de equipo**, validar su formato y consultar estado/ubicación actuales vía API. No confiar en datos de disponibilidad contenidos en el QR impreso.

## Alternativas
1. Abrir una app lectora externa: reduce el código, pero rompe experiencia y dificulta gestión de errores.
2. Codificar disponibilidad directamente en el QR: descartado porque queda desactualizada.
3. Implementar la cámara con una biblioteca multiplataforma: reevaluable en caso de extender el MVP a iOS.

## Consecuencias
- **+** Identificador estable y consulta actualizada desde backend.
- **+** Permite mostrar errores controlados cuando un QR no existe.
- **-** Requiere permiso de cámara, pruebas reales y manejo de conectividad.
- **-** CameraX no resuelve por sí solo la ruta de iOS en KMP.

## Verificación
CP-03: comprobar diálogo de permiso de cámara (planificado). CP-07: 20 QR válidos y 5 inválidos; ID, respuesta de API y pantalla deben coincidir; 0 asignaciones incorrectas. **No declarar aprobado sin ejecución y evidencia.**
