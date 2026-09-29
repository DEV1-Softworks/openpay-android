# Architecture

The SDK follows **clean architecture**: the domain rules sit at the center
and never depend on Android, the network or any framework. Outer layers
depend inward, which keeps the business logic testable in plain JVM tests.

## Modules

```mermaid
flowchart LR
    subgraph Repository
        app[":app — sample application"]
        sdk[":openpay-sdk — the published library"]
    end
    app -->|consumes| sdk
    sdk -->|published as| maven[("Maven: mx.dev1.openpay:openpay-sdk")]
```

- **`:openpay-sdk`** — everything a merchant app needs: networking,
  tokenization, validation, antifraud, Compose UI and translations.
- **`:app`** — a runnable sample that demonstrates every SDK feature.

## Layers inside the SDK

```mermaid
flowchart TB
    subgraph presentation["UI layer (Compose)"]
        form["OpenpayCardForm / fields"]
        formState["OpenpayCardFormState"]
        xml["OpenpayCardFormView (XML interop)"]
    end
    subgraph facade["Public facade"]
        openpayFacade["Openpay"]
    end
    subgraph domain["Domain layer (pure Kotlin)"]
        models["Card / Token / Address"]
        validator["CardValidator / CardBrand"]
        usecase["CreateTokenUseCase"]
        repoInterface["TokenRepository (interface)"]
    end
    subgraph data["Data layer"]
        repoImpl["RemoteTokenRepository"]
        http["Ktor HttpClient (CIO)"]
        dto["DTOs + mappers"]
    end
    subgraph antifraud["Antifraud"]
        collector["WebViewDeviceSessionCollector"]
    end

    form --> formState --> validator
    openpayFacade --> usecase
    openpayFacade --> collector
    usecase --> validator
    usecase --> repoInterface
    repoImpl -.implements.-> repoInterface
    repoImpl --> http
    repoImpl --> dto
```

Key rules:

- The **domain layer** (`domain/`) has no Android imports. `CardValidator`
  and `CreateTokenUseCase` run in milliseconds on any JVM.
- The **data layer** (`data/`) implements the domain interfaces using Ktor
  and kotlinx.serialization. Swapping the HTTP stack would touch only this
  layer.
- The **UI layer** (`ui/`) is Compose-first. The XML wrapper exists purely
  for legacy host apps.
- **Koin** wires everything inside an *isolated* container
  (`OpenpayKoinContext`), so the SDK never conflicts with a host app that
  also uses Koin.

## Tokenization flow

```mermaid
sequenceDiagram
    actor User
    participant Form as OpenpayCardForm
    participant Facade as Openpay
    participant UseCase as CreateTokenUseCase
    participant Validator as CardValidator
    participant Repo as RemoteTokenRepository
    participant API as Openpay API

    User->>Form: types card data
    Form->>Form: live filtering + brand detection
    User->>Form: taps "Save card"
    Form->>Facade: createToken(card)
    Facade->>UseCase: invoke(card)
    UseCase->>Validator: validate(card)
    alt card invalid
        Validator-->>UseCase: invalid fields
        UseCase-->>Facade: ValidationError (no network call)
    else card valid
        UseCase->>Repo: createToken(card)
        Repo->>API: POST /v1/{merchantId}/tokens
        API-->>Repo: token JSON
        Repo-->>UseCase: Token
        UseCase-->>Facade: Token
    end
    Facade-->>Form: Result<Token>
```

## Antifraud flow

```mermaid
sequenceDiagram
    participant App as Merchant app
    participant Facade as Openpay
    participant Collector as WebViewDeviceSessionCollector
    participant API as Openpay fingerprint page

    App->>Facade: setupDeviceSession(activity)
    Facade->>Collector: collect(activity)
    Collector->>Collector: generate 32-char session id
    Collector->>API: hidden WebView loads /oa/logo.htm?m={merchant}&s={session}
    Collector-->>Facade: session id (immediately)
    Note over Collector: WebView destroys itself when the page finishes
    Facade-->>App: deviceSessionId → send to your backend with the token
```

## Package map

| Package | Contents |
|---|---|
| `mx.dev1.openpay.sdk` | `Openpay` facade, `OpenpayCallback`, `OpenpaySdk` |
| `...sdk.core` | `OpenpayConfig`, countries, environments, `OpenpayException` |
| `...sdk.domain.model` | `Card`, `Token`, `TokenizedCard`, `Address` |
| `...sdk.domain.validation` | `CardValidator`, `CardBrand`, `CardValidationResult` |
| `...sdk.domain.usecase` / `repository` | `CreateTokenUseCase`, `TokenRepository` |
| `...sdk.data` | Ktor client factory, DTOs, mappers, `RemoteTokenRepository` |
| `...sdk.antifraud` | Device session collection |
| `...sdk.ui` | Theme, form, fields, visual transformations, XML view |
| `...sdk.i18n` | `OpenpayLanguage`, `OpenpayLocale` override |
| `...sdk.di` | Koin modules and the isolated container |
