# Integración continua

El repositorio ejecuta un pipeline de GitHub Actions (`.github/workflows/ci.yml`) en cada push y pull request dirigidos a `develop` o `master`. No necesitas instalar nada: GitHub lo ejecuta automáticamente cuando abres un pull request.

## Qué hace el pipeline

```mermaid
flowchart LR
    A[Push o pull request] --> B[Pruebas unitarias y cobertura]
    A --> C[Pruebas instrumentadas]
    B --> D[Reportes JaCoCo XML + HTML]
    D --> E[Comentario de cobertura en el pull request]
    D --> F[Umbral de 80% de cobertura de líneas para openpay-sdk]
    C --> G[Reportes de pruebas en emulador]
```

### Pruebas unitarias y cobertura

Este job se ejecuta en cada push y pull request:

1. Compila el proyecto con JDK 17 y ejecuta `testDebugUnitTest` en todos los módulos.
2. Genera los reportes de cobertura de JaCoCo (`jacocoTestReport`).
3. Aplica el mínimo de 80% de cobertura de líneas en `openpay-sdk` (`jacocoCoverageVerification`). El build falla cuando la cobertura queda por debajo de ese umbral.
4. En los pull requests publica (y mantiene actualizado) un comentario con la cobertura general y la cobertura de los archivos modificados por el pull request, usando [Madrapps/jacoco-report](https://github.com/Madrapps/jacoco-report).
5. Sube los reportes HTML de cobertura y de pruebas como artefactos del build, para que puedas descargarlos y revisarlos desde la página de la ejecución.

### Pruebas instrumentadas

Este job arranca un emulador de Android 14 (API 34) en el runner y ejecuta `connectedDebugAndroidTest`. El emulador necesita aceleración por hardware, por eso el job habilita KVM antes de iniciarlo. Los reportes de pruebas se suben como artefactos.

## Cómo leer el comentario de cobertura

Cada pull request recibe un único comentario titulado **Unit test coverage** que se actualiza en cada push. Muestra:

- **Cobertura general** del proyecto, marcada con ✅ cuando está en 80% o más.
- **Cobertura de los archivos modificados**, para que quienes revisan vean si el código nuevo está probado sin abrir el reporte completo.

## Ejecutar las mismas verificaciones en local

```bash
./gradlew testDebugUnitTest jacocoTestReport :openpay-sdk:jacocoCoverageVerification
./gradlew connectedDebugAndroidTest   # requiere un dispositivo o emulador
```

El reporte HTML se genera en `<módulo>/build/reports/jacoco/jacocoTestReport/html/index.html`.