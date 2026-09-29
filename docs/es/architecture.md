# Arquitectura

El SDK sigue la **arquitectura limpia**: las reglas de dominio están en el
centro y nunca dependen de Android, de la red ni de ningún framework. Las
capas externas dependen hacia adentro, lo que mantiene la lógica de negocio
testeable en pruebas de JVM puras.

## Módulos

```mermaid
flowchart LR
    subgraph Repositorio
        app[":app — aplicación de ejemplo"]
        sdk[":openpay-sdk — la librería publicada"]
    end
    app -->|consume| sdk
    sdk -->|publicada como| maven[("Maven: mx.dev1.openpay:openpay-sdk")]
```

- **`:openpay-sdk`** — todo lo que necesita la app de un comercio: red,
  tokenización, validación, antifraude, UI de Compose y traducciones.
- **`:app`** — un ejemplo ejecutable que demuestra cada funcionalidad del
  SDK.

## Capas dentro del SDK

```mermaid
flowchart TB
    subgraph presentation["Capa de UI (Compose)"]
        form["OpenpayCardForm / campos"]
        formState["OpenpayCardFormState"]
        xml["OpenpayCardFormView (interoperabilidad con XML)"]
    end
    subgraph facade["Fachada pública"]
        openpayFacade["Openpay"]
    end
    subgraph domain["Capa de dominio (Kotlin puro)"]
        models["Card / Token / Address"]
        validator["CardValidator / CardBrand"]
        usecase["CreateTokenUseCase"]
        repoInterface["TokenRepository (interfaz)"]
    end
    subgraph data["Capa de datos"]
        repoImpl["RemoteTokenRepository"]
        http["Ktor HttpClient (CIO)"]
        dto["DTOs + mappers"]
    end
    subgraph antifraud["Antifraude"]
        collector["WebViewDeviceSessionCollector"]
    end

    form --> formState --> validator
    openpayFacade --> usecase
    openpayFacade --> collector
    usecase --> validator
    usecase --> repoInterface
    repoImpl -.implementa.-> repoInterface
    repoImpl --> http
    repoImpl --> dto
```

Reglas clave:

- La **capa de dominio** (`domain/`) no tiene imports de Android.
  `CardValidator` y `CreateTokenUseCase` se ejecutan en milisegundos en
  cualquier JVM.
- La **capa de datos** (`data/`) implementa las interfaces del dominio
  usando Ktor y kotlinx.serialization. Cambiar el stack HTTP tocaría solo
  esta capa.
- La **capa de UI** (`ui/`) es Compose-first. El wrapper de XML existe
  únicamente para apps anfitrionas legadas.
- **Koin** conecta todo dentro de un contenedor *aislado*
  (`OpenpayKoinContext`), de modo que el SDK nunca entra en conflicto con
  una app anfitriona que también use Koin.

## Flujo de tokenización

```mermaid
sequenceDiagram
    actor Usuario
    participant Form as OpenpayCardForm
    participant Facade as Openpay
    participant UseCase as CreateTokenUseCase
    participant Validator as CardValidator
    participant Repo as RemoteTokenRepository
    participant API as Openpay API

    Usuario->>Form: escribe los datos de la tarjeta
    Form->>Form: filtrado en vivo + detección de marca
    Usuario->>Form: pulsa "Guardar tarjeta"
    Form->>Facade: createToken(card)
    Facade->>UseCase: invoke(card)
    UseCase->>Validator: validate(card)
    alt tarjeta inválida
        Validator-->>UseCase: campos inválidos
        UseCase-->>Facade: ValidationError (sin llamada de red)
    else tarjeta válida
        UseCase->>Repo: createToken(card)
        Repo->>API: POST /v1/{merchantId}/tokens
        API-->>Repo: JSON del token
        Repo-->>UseCase: Token
        UseCase-->>Facade: Token
    end
    Facade-->>Form: Result<Token>
```

## Flujo antifraude

```mermaid
sequenceDiagram
    participant App as App del comercio
    participant Facade as Openpay
    participant Collector as WebViewDeviceSessionCollector
    participant API as Página de huella de Openpay

    App->>Facade: setupDeviceSession(activity)
    Facade->>Collector: collect(activity)
    Collector->>Collector: genera un id de sesión de 32 caracteres
    Collector->>API: un WebView oculto carga /oa/logo.htm?m={merchant}&s={session}
    Collector-->>Facade: id de sesión (de inmediato)
    Note over Collector: el WebView se destruye a sí mismo cuando la página termina
    Facade-->>App: deviceSessionId → envíalo a tu backend con el token
```

## Mapa de paquetes

| Paquete | Contenido |
|---|---|
| `mx.dev1.openpay.sdk` | Fachada `Openpay`, `OpenpayCallback`, `OpenpaySdk` |
| `...sdk.core` | `OpenpayConfig`, países, entornos, `OpenpayException` |
| `...sdk.domain.model` | `Card`, `Token`, `TokenizedCard`, `Address` |
| `...sdk.domain.validation` | `CardValidator`, `CardBrand`, `CardValidationResult` |
| `...sdk.domain.usecase` / `repository` | `CreateTokenUseCase`, `TokenRepository` |
| `...sdk.data` | Fábrica del cliente Ktor, DTOs, mappers, `RemoteTokenRepository` |
| `...sdk.antifraud` | Recolección de la sesión de dispositivo |
| `...sdk.ui` | Tema, formulario, campos, transformaciones visuales, vista XML |
| `...sdk.i18n` | `OpenpayLanguage`, override de `OpenpayLocale` |
| `...sdk.di` | Módulos de Koin y el contenedor aislado |
