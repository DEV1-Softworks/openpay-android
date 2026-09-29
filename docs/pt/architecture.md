# Arquitetura

O SDK segue a **arquitetura limpa** (clean architecture): as regras de
domínio ficam no centro e nunca dependem do Android, da rede ou de qualquer
framework. As camadas externas dependem das internas, o que mantém a lógica
de negócio testável em testes JVM puros.

## Módulos

```mermaid
flowchart LR
    subgraph Repository["Repositório"]
        app[":app — aplicativo de exemplo"]
        sdk[":openpay-sdk — a biblioteca publicada"]
    end
    app -->|consome| sdk
    sdk -->|publicado como| maven[("Maven: mx.dev1.openpay:openpay-sdk")]
```

- **`:openpay-sdk`** — tudo o que o app de um comércio precisa: rede,
  tokenização, validação, antifraude, UI em Compose e traduções.
- **`:app`** — um exemplo executável que demonstra todos os recursos do SDK.

## Camadas dentro do SDK

```mermaid
flowchart TB
    subgraph presentation["Camada de UI (Compose)"]
        form["OpenpayCardForm / campos"]
        formState["OpenpayCardFormState"]
        xml["OpenpayCardFormView (interop XML)"]
    end
    subgraph facade["Fachada pública"]
        openpayFacade["Openpay"]
    end
    subgraph domain["Camada de domínio (Kotlin puro)"]
        models["Card / Token / Address"]
        validator["CardValidator / CardBrand"]
        usecase["CreateTokenUseCase"]
        repoInterface["TokenRepository (interface)"]
    end
    subgraph data["Camada de dados"]
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

Regras principais:

- A **camada de domínio** (`domain/`) não tem imports do Android.
  `CardValidator` e `CreateTokenUseCase` executam em milissegundos em
  qualquer JVM.
- A **camada de dados** (`data/`) implementa as interfaces do domínio usando
  Ktor e kotlinx.serialization. Trocar a pilha HTTP afetaria apenas esta
  camada.
- A **camada de UI** (`ui/`) é Compose em primeiro lugar. O wrapper XML
  existe apenas para apps hospedeiros legados.
- O **Koin** conecta tudo dentro de um contêiner *isolado*
  (`OpenpayKoinContext`), de modo que o SDK nunca entra em conflito com um
  app hospedeiro que também use Koin.

## Fluxo de tokenização

```mermaid
sequenceDiagram
    actor User as Usuário
    participant Form as OpenpayCardForm
    participant Facade as Openpay
    participant UseCase as CreateTokenUseCase
    participant Validator as CardValidator
    participant Repo as RemoteTokenRepository
    participant API as Openpay API

    User->>Form: digita os dados do cartão
    Form->>Form: filtragem em tempo real + detecção de bandeira
    User->>Form: toca em "Salvar cartão"
    Form->>Facade: createToken(card)
    Facade->>UseCase: invoke(card)
    UseCase->>Validator: validate(card)
    alt cartão inválido
        Validator-->>UseCase: campos inválidos
        UseCase-->>Facade: ValidationError (sem chamada de rede)
    else cartão válido
        UseCase->>Repo: createToken(card)
        Repo->>API: POST /v1/{merchantId}/tokens
        API-->>Repo: JSON do token
        Repo-->>UseCase: Token
        UseCase-->>Facade: Token
    end
    Facade-->>Form: Result<Token>
```

## Fluxo antifraude

```mermaid
sequenceDiagram
    participant App as App do comércio
    participant Facade as Openpay
    participant Collector as WebViewDeviceSessionCollector
    participant API as Página de fingerprint da Openpay

    App->>Facade: setupDeviceSession(activity)
    Facade->>Collector: collect(activity)
    Collector->>Collector: gera um id de sessão de 32 caracteres
    Collector->>API: WebView oculta carrega /oa/logo.htm?m={merchant}&s={session}
    Collector-->>Facade: id de sessão (imediatamente)
    Note over Collector: a WebView se destrói quando a página termina de carregar
    Facade-->>App: deviceSessionId → envie ao seu backend com o token
```

## Mapa de pacotes

| Pacote | Conteúdo |
|---|---|
| `mx.dev1.openpay.sdk` | Fachada `Openpay`, `OpenpayCallback`, `OpenpaySdk` |
| `...sdk.core` | `OpenpayConfig`, países, ambientes, `OpenpayException` |
| `...sdk.domain.model` | `Card`, `Token`, `TokenizedCard`, `Address` |
| `...sdk.domain.validation` | `CardValidator`, `CardBrand`, `CardValidationResult` |
| `...sdk.domain.usecase` / `repository` | `CreateTokenUseCase`, `TokenRepository` |
| `...sdk.data` | Fábrica do cliente Ktor, DTOs, mappers, `RemoteTokenRepository` |
| `...sdk.antifraud` | Coleta da sessão do dispositivo |
| `...sdk.ui` | Tema, formulário, campos, transformações visuais, view XML |
| `...sdk.i18n` | `OpenpayLanguage`, substituição via `OpenpayLocale` |
| `...sdk.di` | Módulos Koin e o contêiner isolado |
