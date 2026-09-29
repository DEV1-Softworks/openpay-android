# Architecture

Le SDK suit les principes de la **clean architecture** : les règles du
domaine sont au centre et ne dépendent jamais d'Android, du réseau ni
d'aucun framework. Les couches externes dépendent vers l'intérieur, ce qui
garde la logique métier testable dans de simples tests JVM.

## Modules

```mermaid
flowchart LR
    subgraph Repository["Dépôt"]
        app[":app — application d'exemple"]
        sdk[":openpay-sdk — la bibliothèque publiée"]
    end
    app -->|consomme| sdk
    sdk -->|publié sous| maven[("Maven: mx.dev1.openpay:openpay-sdk")]
```

- **`:openpay-sdk`** — tout ce dont une application marchande a besoin :
  réseau, tokenisation, validation, antifraude, interface Compose et
  traductions.
- **`:app`** — un exemple exécutable qui illustre chaque fonctionnalité du
  SDK.

## Couches à l'intérieur du SDK

```mermaid
flowchart TB
    subgraph presentation["Couche UI (Compose)"]
        form["OpenpayCardForm / champs"]
        formState["OpenpayCardFormState"]
        xml["OpenpayCardFormView (interop XML)"]
    end
    subgraph facade["Façade publique"]
        openpayFacade["Openpay"]
    end
    subgraph domain["Couche domaine (Kotlin pur)"]
        models["Card / Token / Address"]
        validator["CardValidator / CardBrand"]
        usecase["CreateTokenUseCase"]
        repoInterface["TokenRepository (interface)"]
    end
    subgraph data["Couche données"]
        repoImpl["RemoteTokenRepository"]
        http["Ktor HttpClient (CIO)"]
        dto["DTO + mappers"]
    end
    subgraph antifraud["Antifraude"]
        collector["WebViewDeviceSessionCollector"]
    end

    form --> formState --> validator
    openpayFacade --> usecase
    openpayFacade --> collector
    usecase --> validator
    usecase --> repoInterface
    repoImpl -.implémente.-> repoInterface
    repoImpl --> http
    repoImpl --> dto
```

Règles clés :

- La **couche domaine** (`domain/`) ne contient aucun import Android.
  `CardValidator` et `CreateTokenUseCase` s'exécutent en quelques
  millisecondes sur n'importe quelle JVM.
- La **couche données** (`data/`) implémente les interfaces du domaine
  avec Ktor et kotlinx.serialization. Remplacer la pile HTTP ne toucherait
  que cette couche.
- La **couche UI** (`ui/`) est pensée d'abord pour Compose. Le wrapper XML
  existe uniquement pour les applications hôtes historiques.
- **Koin** assemble le tout dans un conteneur *isolé*
  (`OpenpayKoinContext`), de sorte que le SDK n'entre jamais en conflit
  avec une application hôte qui utilise elle aussi Koin.

## Flux de tokenisation

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Form as OpenpayCardForm
    participant Facade as Openpay
    participant UseCase as CreateTokenUseCase
    participant Validator as CardValidator
    participant Repo as RemoteTokenRepository
    participant API as API Openpay

    User->>Form: saisit les données de la carte
    Form->>Form: filtrage en direct + détection de la marque
    User->>Form: appuie sur « Enregistrer la carte »
    Form->>Facade: createToken(card)
    Facade->>UseCase: invoke(card)
    UseCase->>Validator: validate(card)
    alt carte invalide
        Validator-->>UseCase: champs invalides
        UseCase-->>Facade: ValidationError (aucun appel réseau)
    else carte valide
        UseCase->>Repo: createToken(card)
        Repo->>API: POST /v1/{merchantId}/tokens
        API-->>Repo: JSON du token
        Repo-->>UseCase: Token
        UseCase-->>Facade: Token
    end
    Facade-->>Form: Result<Token>
```

## Flux antifraude

```mermaid
sequenceDiagram
    participant App as Application marchande
    participant Facade as Openpay
    participant Collector as WebViewDeviceSessionCollector
    participant API as Page d'empreinte Openpay

    App->>Facade: setupDeviceSession(activity)
    Facade->>Collector: collect(activity)
    Collector->>Collector: génère un id de session de 32 caractères
    Collector->>API: une WebView cachée charge /oa/logo.htm?m={merchant}&s={session}
    Collector-->>Facade: id de session (immédiatement)
    Note over Collector: La WebView se détruit elle-même quand la page a fini de charger
    Facade-->>App: deviceSessionId → à envoyer à votre backend avec le token
```

## Carte des packages

| Package | Contenu |
|---|---|
| `mx.dev1.openpay.sdk` | Façade `Openpay`, `OpenpayCallback`, `OpenpaySdk` |
| `...sdk.core` | `OpenpayConfig`, pays, environnements, `OpenpayException` |
| `...sdk.domain.model` | `Card`, `Token`, `TokenizedCard`, `Address` |
| `...sdk.domain.validation` | `CardValidator`, `CardBrand`, `CardValidationResult` |
| `...sdk.domain.usecase` / `repository` | `CreateTokenUseCase`, `TokenRepository` |
| `...sdk.data` | Fabrique de client Ktor, DTO, mappers, `RemoteTokenRepository` |
| `...sdk.antifraud` | Collecte de la session d'appareil |
| `...sdk.ui` | Thème, formulaire, champs, transformations visuelles, vue XML |
| `...sdk.i18n` | `OpenpayLanguage`, forçage `OpenpayLocale` |
| `...sdk.di` | Modules Koin et le conteneur isolé |
