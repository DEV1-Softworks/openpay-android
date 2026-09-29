# Getting started

This guide takes you from zero to your first card token. No prior Android
experience is required: every step is spelled out.

> **What is a token?** Card data is sensitive, so your app never sends it to
> your own servers. Instead, the SDK exchanges the card for a one-time
> **token** directly with Openpay. Your backend then uses that token to
> create the charge. Your servers never see the card number.

## Requirements

| Requirement | Minimum |
|---|---|
| Android Studio | Narwhal (2025.1) or newer |
| JDK | 17 (Android Studio bundles one) |
| Android device / emulator | Android 8.0 (API 26) or newer |
| Openpay account | Merchant ID + **public** API key from the [Openpay dashboard](https://www.openpay.mx) |

## 1. Install the SDK

Add the dependency to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("mx.dev1.openpay:openpay-sdk:1.0.0")
}
```

The SDK already declares the `INTERNET` permission in its manifest; you do
not need to add anything else.

## 2. Configure the SDK

Create one `Openpay` instance with your merchant credentials:

```kotlin
import mx.dev1.openpay.sdk.Openpay
import mx.dev1.openpay.sdk.core.OpenpayConfig
import mx.dev1.openpay.sdk.core.OpenpayCountry
import mx.dev1.openpay.sdk.core.OpenpayEnvironment

val openpay = Openpay(
    OpenpayConfig(
        merchantId = "your-merchant-id",
        publicApiKey = "pk_your_public_key",
        country = OpenpayCountry.MEXICO,          // MEXICO, COLOMBIA or PERU
        environment = OpenpayEnvironment.SANDBOX, // SANDBOX while testing
    )
)
```

> **Never** ship your *private* API key inside the app. Only the public key
> is safe on devices.

## 3. Capture the card and create a token

The fastest path is the ready-made Compose form, which validates every
field and hands you a `Card` object only when the data is correct:

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

Prefer to build your own UI? Create the `Card` yourself and validate it
with `CardValidator`:

```kotlin
import mx.dev1.openpay.sdk.domain.model.Card

val card = Card(
    holderName = "Juan Pérez Ramírez",
    cardNumber = "4111111111111111",
    expirationMonth = 12,
    expirationYear = 30,
    securityCode = "110",
)
val result = openpay.createToken(card) // suspend fun returning Result<Token>
```

Consumers without coroutines can use the callback flavor,
`openpay.createToken(card, callback)`; the callback runs on the main thread.

## 4. Start the antifraud device session

Openpay's risk engine needs a device fingerprint per checkout. Collect it
once per payment screen and send the returned id to your backend together
with the token:

```kotlin
val deviceSessionId = openpay.setupDeviceSession(activity)
```

## 5. Handle errors

Every failure is an `OpenpayException`:

| Type | Meaning | Typical reaction |
|---|---|---|
| `ValidationError` | The card failed local validation; no network call happened | Highlight `validationResult.invalidFields` |
| `ServiceError` | Openpay rejected the request (`errorCode`, `category`, `requestId`) | Show a message, log the `requestId` |
| `ConnectionError` | The API was unreachable | Offer a retry |

## 6. Test cards (sandbox)

| Number | Brand |
|---|---|
| 4111 1111 1111 1111 | Visa |
| 5555 5555 5555 4444 | Mastercard |
| 3782 822463 10005 | American Express |

Any future expiration date and any well-formed security code work in
sandbox.

## Next steps

- [Architecture](architecture.md) — how the SDK is organized inside
- [UI components](ui-components.md) — the Compose form, XML interop and accessibility
- [Internationalization](internationalization.md) — languages and overrides
- [Contributing](contributing.md) — building and testing the project yourself
