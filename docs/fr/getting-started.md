# Premiers pas

Ce guide vous accompagne de zéro jusqu'à votre premier token de carte.
Aucune expérience Android préalable n'est requise : chaque étape est
détaillée.

> **Qu'est-ce qu'un token ?** Les données de carte sont sensibles, votre
> application ne les envoie donc jamais à vos propres serveurs. À la place,
> le SDK échange la carte contre un **token** à usage unique directement
> auprès d'Openpay. Votre backend utilise ensuite ce token pour créer le
> paiement. Vos serveurs ne voient jamais le numéro de carte.

## Prérequis

| Prérequis | Minimum |
|---|---|
| Android Studio | Narwhal (2025.1) ou plus récent |
| JDK | 17 (Android Studio en inclut un) |
| Appareil / émulateur Android | Android 8.0 (API 26) ou plus récent |
| Compte Openpay | Identifiant marchand + clé API **publique** depuis le [tableau de bord Openpay](https://www.openpay.mx) |

## 1. Installer le SDK

Ajoutez la dépendance au fichier `build.gradle.kts` de votre module :

```kotlin
dependencies {
    implementation("mx.dev1.openpay:openpay-sdk:1.0.0")
}
```

Le SDK déclare déjà la permission `INTERNET` dans son manifeste ; vous
n'avez rien d'autre à ajouter.

## 2. Configurer le SDK

Créez une instance `Openpay` avec vos identifiants marchand :

```kotlin
import mx.dev1.openpay.sdk.Openpay
import mx.dev1.openpay.sdk.core.OpenpayConfig
import mx.dev1.openpay.sdk.core.OpenpayCountry
import mx.dev1.openpay.sdk.core.OpenpayEnvironment

val openpay = Openpay(
    OpenpayConfig(
        merchantId = "your-merchant-id",
        publicApiKey = "pk_your_public_key",
        country = OpenpayCountry.MEXICO,          // MEXICO, COLOMBIA ou PERU
        environment = OpenpayEnvironment.SANDBOX, // SANDBOX pendant les tests
    )
)
```

> N'embarquez **jamais** votre clé API *privée* dans l'application. Seule
> la clé publique peut être présente en toute sécurité sur les appareils.

## 3. Saisir la carte et créer un token

Le chemin le plus rapide est le formulaire Compose prêt à l'emploi, qui
valide chaque champ et ne vous remet un objet `Card` que lorsque les
données sont correctes :

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

Vous préférez construire votre propre interface ? Créez la `Card`
vous-même et validez-la avec `CardValidator` :

```kotlin
import mx.dev1.openpay.sdk.domain.model.Card

val card = Card(
    holderName = "Juan Pérez Ramírez",
    cardNumber = "4111111111111111",
    expirationMonth = 12,
    expirationYear = 30,
    securityCode = "110",
)
val result = openpay.createToken(card) // suspend fun renvoyant Result<Token>
```

Les intégrations sans coroutines peuvent utiliser la variante à callback,
`openpay.createToken(card, callback)` ; le callback s'exécute sur le
thread principal.

## 4. Démarrer la session d'appareil antifraude

Le moteur de risque d'Openpay a besoin d'une empreinte de l'appareil par
paiement. Collectez-la une fois par écran de paiement et envoyez
l'identifiant retourné à votre backend avec le token :

```kotlin
val deviceSessionId = openpay.setupDeviceSession(activity)
```

## 5. Gérer les erreurs

Chaque échec est une `OpenpayException` :

| Type | Signification | Réaction typique |
|---|---|---|
| `ValidationError` | La carte a échoué à la validation locale ; aucun appel réseau n'a eu lieu | Mettre en évidence `validationResult.invalidFields` |
| `ServiceError` | Openpay a rejeté la requête (`errorCode`, `category`, `requestId`) | Afficher un message, journaliser le `requestId` |
| `ConnectionError` | L'API était injoignable | Proposer une nouvelle tentative |

## 6. Cartes de test (sandbox)

| Numéro | Marque |
|---|---|
| 4111 1111 1111 1111 | Visa |
| 5555 5555 5555 4444 | Mastercard |
| 3782 822463 10005 | American Express |

Toute date d'expiration future et tout code de sécurité bien formé
fonctionnent en sandbox.

## Étapes suivantes

- [Architecture](architecture.md) — comment le SDK est organisé en interne
- [Composants d'interface](ui-components.md) — le formulaire Compose, l'interopérabilité XML et l'accessibilité
- [Internationalisation](internationalization.md) — langues et forçages
- [Contribuer](contributing.md) — compiler et tester le projet vous-même
