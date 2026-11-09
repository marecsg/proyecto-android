# ADR-001: Cliente Kotlin Multiplatform, API Spring Boot y MySQL

- **Estado:** Adoptada como base técnica; integración completa pendiente de prueba.
- **Fecha de registro:** 2026-11-09.
- **Responsable de validación:** Arquitecto del equipo (designación por confirmar).
- **Relaciones:** RF-01, RF-02, RF-03, RF-05, RF-06, RF-07; R3; SC-02.

## Contexto
El Project Charter define un MVP operativo para préstamos, devoluciones e incidencias por QR. El código de `proyecto-android` presenta módulos `androidApp`, `iosApp` y `shared`, con capas `presentation`, `domain` y `data`; se conecta vía Ktor. El repositorio `proyecto-backend` contiene Spring Boot, entidades, servicios, controladores, Spring Security y MySQL. Aunque el Charter menciona Android/Jetpack Compose, el cliente publicado usa Kotlin Multiplatform y Compose Multiplatform. Esta diferencia debe ser aceptada por el equipo/docente.

## Decisión
Mantener como **arquitectura documentada al corte** una app Kotlin Multiplatform con Clean Architecture, una API REST Spring Boot con separación Controller/Service/Repository, y MySQL gestionada mediante JPA. Se **descarta afirmar** que existen microservicios, API Gateway, Kubernetes o un despliegue productivo: no hay evidencias de ellos en los repositorios revisados.

## Alternativas consideradas
1. Flutter + API Spring Boot: exige migrar interfaz ya desarrollada.
2. Android nativo sin código compartido: reduce la complejidad inicial, pero descartaría la estructura `shared` actual.
3. Backend en microservicios: mayor aislamiento, pero complejidad operativa innecesaria para cuatro integrantes y un MVP.

## Consecuencias
- **+** Reutilización de lógica entre targets y separación de responsabilidades.
- **+** Contratos HTTP y datos centralizados en backend.
- **-** El equipo debe dominar KMP y configurar motores HTTP por plataforma.
- **-** Es necesario coordinar cambios de API y validar contratos en ambos repositorios.

## Evidencia y verificación pendiente
- Android: `shared/src/commonMain/kotlin/com/pe/...` y `shared/build.gradle.kts`.
- Backend: `pom.xml`, paquetes `Controller`, `Services`, `Repository` y `Entity`.
- **Pendiente:** probar build, login, catálogo y solicitud extremo a extremo con backend actualizado. El `pom.xml` subido declara Spring Boot 3.2.5, una rama sin soporte OSS en 2026; migración de versión por planificar.
