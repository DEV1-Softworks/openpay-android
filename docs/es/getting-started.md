# Primeros pasos

Esta guía te lleva desde cero hasta tu primer token de tarjeta. No se
requiere experiencia previa en Android: cada paso está explicado en detalle.

> **¿Qué es un token?** Los datos de la tarjeta son sensibles, por lo que tu
> app nunca los envía a tus propios servidores. En su lugar, el SDK
> intercambia la tarjeta por un **token** de un solo uso directamente con
> Openpay. Tu backend usa después ese token para crear el cargo. Tus
> servidores nunca ven el número de tarjeta.

## Requisitos

| Requisito | Mínimo |
|---|---|
| Android Studio | Narwhal (2025.1) o más reciente |
| JDK | 17 (Android Studio incluye uno) |
| Dispositivo / emulador Android | Android 8.0 (API 26) o más reciente |
| Cuenta de Openpay | Merchant ID + clave de API **pública** desde el [panel de Openpay](https://www.openpay.mx) |

## 1. Instala el SDK

Agrega la dependencia al `build.gradle.kts` de tu módulo:

```kotlin
dependencies {
    implementation("mx.dev1.openpay:openpay-sdk:1.0.0")
}
```

El SDK ya declara el permiso `INTERNET` en su manifiesto; no necesitas
agregar nada más.

## 2. Configura el SDK

Crea una instancia de `Openpay` con las credenciales de tu comercio:

```kotlin
import mx.dev1.openpay.sdk.Openpay
import mx.dev1.openpay.sdk.core.OpenpayConfig
import mx.dev1.openpay.sdk.core.OpenpayCountry
import mx.dev1.openpay.sdk.core.OpenpayEnvironment

val openpay = Openpay(
    OpenpayConfig(
        merchantId = "your-merchant-id",
        publicApiKey = "pk_your_public_key",
        country = OpenpayCountry.MEXICO,          // MEXICO, COLOMBIA o PERU
        environment = OpenpayEnvironment.SANDBOX, // SANDBOX mientras pruebas
    )
)
```

> **Nunca** incluyas tu clave de API *privada* dentro de la app. Solo la
> clave pública es segura en los dispositivos.

## 3. Captura la tarjeta y crea un token

El camino más rápido es el formulario de Compose listo para usar, que valida
cada campo y te entrega un objeto `Card` solo cuando los datos son
correctos:

```kotlin
import mx.dev1.openpay.sdk.ui.components.OpenpayCardForm

OpenpayCardForm(
    onCardValidated = { card ->
        scope.launch {
            openpay.createToken(card)
                .onSuccess { token -> sendToBackend(token.tokenId) }
                .onFailure { error -> showError(error) }
        }
    }
)
```

¿Prefieres construir tu propia UI? Crea el `Card` tú mismo y valídalo con
`CardValidator`:

```kotlin
import mx.dev1.openpay.sdk.domain.model.Card

val card = Card(
    holderName = "Juan Pérez Ramírez",
    cardNumber = "4111111111111111",
    expirationMonth = 12,
    expirationYear = 30,
    securityCode = "110",
)
val result = openpay.createToken(card) // suspend fun que devuelve Result<Token>
```

Los consumidores sin corrutinas pueden usar la variante con callback,
`openpay.createToken(card, callback)`; el callback se ejecuta en el hilo
principal.

## 4. Inicia la sesión de dispositivo antifraude

El motor de riesgo de Openpay necesita una huella del dispositivo por cada
checkout. Recoléctala una vez por pantalla de pago y envía el id devuelto a
tu backend junto con el token:

```kotlin
val deviceSessionId = openpay.setupDeviceSession(activity)
```

## 5. Maneja los errores

Toda falla es una `OpenpayException`:

| Tipo | Significado | Reacción típica |
|---|---|---|
| `ValidationError` | La tarjeta no pasó la validación local; no hubo llamada de red | Resaltar `validationResult.invalidFields` |
| `ServiceError` | Openpay rechazó la solicitud (`errorCode`, `category`, `requestId`) | Mostrar un mensaje, registrar el `requestId` |
| `ConnectionError` | No se pudo alcanzar la API | Ofrecer un reintento |

## 6. Tarjetas de prueba (sandbox)

| Número | Marca |
|---|---|
| 4111 1111 1111 1111 | Visa |
| 5555 5555 5555 4444 | Mastercard |
| 3782 822463 10005 | American Express |

Cualquier fecha de vencimiento futura y cualquier código de seguridad bien
formado funcionan en sandbox.

## Próximos pasos

- [Arquitectura](architecture.md) — cómo está organizado el SDK por dentro
- [Componentes de UI](ui-components.md) — el formulario de Compose, interoperabilidad con XML y accesibilidad
- [Internacionalización](internationalization.md) — idiomas y overrides
- [Contribuir](contributing.md) — compilar y probar el proyecto tú mismo
