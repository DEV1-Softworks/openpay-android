# Openpay Android SDK

Modern, Kotlin-first Android SDK for [Openpay](https://www.openpay.mx):
card tokenization, validation, antifraud device sessions and ready-made
Jetpack Compose UI — so card data goes straight from the user's device to
Openpay and never touches your servers.

**Documentation languages:**
[English](docs/en/getting-started.md) ·
[Español](docs/es/getting-started.md) ·
[Français](docs/fr/getting-started.md) ·
[Português](docs/pt/getting-started.md)

## Features

- **100% Kotlin**, coroutines-first API with a callback flavor for
  non-coroutine consumers
- **Compose-first UI**: a complete, themeable card form with live brand
  detection, input formatting and full accessibility — plus an
  `AbstractComposeView` wrapper for XML apps
- **Card validation**: Luhn, brand detection (Visa, Mastercard incl.
  2-series, Amex), brand-aware CVV and expiration rules
- **Tokenization** against the Openpay API for Mexico, Colombia and Peru,
  sandbox and production
- **Antifraud device sessions** matching the legacy fingerprint flow
- **4 languages** (en, es, pt, fr) with automatic device-locale resolution
  and a runtime override API
- **Clean architecture + Koin** in an isolated container that never
  clashes with the host app
- **Tested**: JUnit, Mockito and Robolectric with a JaCoCo-enforced 80%
  minimum line coverage

## Installation

```kotlin
dependencies {
    implementation("mx.dev1.openpay:openpay-sdk:1.0.0")
}
```

Minimum Android version: 8.0 (API 26).

## Quick start

```kotlin
// 1. Configure once
val openpay = Openpay(
    OpenpayConfig(
        merchantId = "your-merchant-id",
        publicApiKey = "pk_your_public_key",
        country = OpenpayCountry.MEXICO,
        environment = OpenpayEnvironment.SANDBOX,
    )
)

// 2. Capture the card with the built-in form and tokenize it
OpenpayCardForm(
    onCardValidated = { card ->
        scope.launch {
            openpay.createToken(card)
                .onSuccess { token -> sendToBackend(token.tokenId) }
                .onFailure { error -> showError(error) }
        }
    }
)

// 3. Antifraud device session (send it to your backend with the token)
val deviceSessionId = openpay.setupDeviceSession(activity)
```

A full walkthrough lives in
[docs/en/getting-started.md](docs/en/getting-started.md), and the `:app`
module is a runnable sample of every feature.

## Documentation

| Guide | en | es | fr | pt |
|---|---|---|---|---|
| Getting started | [en](docs/en/getting-started.md) | [es](docs/es/getting-started.md) | [fr](docs/fr/getting-started.md) | [pt](docs/pt/getting-started.md) |
| Architecture (with diagrams) | [en](docs/en/architecture.md) | [es](docs/es/architecture.md) | [fr](docs/fr/architecture.md) | [pt](docs/pt/architecture.md) |
| UI components & accessibility | [en](docs/en/ui-components.md) | [es](docs/es/ui-components.md) | [fr](docs/fr/ui-components.md) | [pt](docs/pt/ui-components.md) |
| Internationalization | [en](docs/en/internationalization.md) | [es](docs/es/internationalization.md) | [fr](docs/fr/internationalization.md) | [pt](docs/pt/internationalization.md) |
| Contributing | [en](docs/en/contributing.md) | [es](docs/es/contributing.md) | [fr](docs/fr/contributing.md) | [pt](docs/pt/contributing.md) |
| Continuous integration | [en](docs/en/ci.md) | [es](docs/es/ci.md) | [fr](docs/fr/ci.md) | [pt](docs/pt/ci.md) |

## Repository layout

```
openpay-android/
├── openpay-sdk/   # the published library (Maven: mx.dev1.openpay:openpay-sdk)
├── app/           # sample application consuming the SDK
├── docs/          # documentation in en / es / fr / pt
└── gradle/libs.versions.toml  # every dependency version, in one place
```

## Contributing

Pull requests are welcome — the project uses Git Flow (`develop` as the
integration branch, `master` as production). Start with
[docs/en/contributing.md](docs/en/contributing.md); it assumes no Android
experience.

## License

Licensed under the [Apache License, Version 2.0](LICENSE.md).
