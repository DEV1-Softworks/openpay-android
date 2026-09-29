# Composants d'interface

Le SDK fournit un kit de saisie de carte pensé d'abord pour Compose. Vous
pouvez utiliser le formulaire complet, des champs individuels, ou intégrer
le formulaire dans une mise en page XML.

## Le formulaire complet

```kotlin
import mx.dev1.openpay.sdk.ui.components.OpenpayCardForm
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme

OpenpayTheme {                       // optionnel : héritez plutôt de votre propre MaterialTheme
    OpenpayCardForm(
        onCardValidated = { card -> /* le tokeniser */ },
    )
}
```

Le formulaire contient le nom du titulaire, le numéro de carte (avec
détection de la marque en direct), l'expiration MM/AA et le code de
sécurité masqué, ainsi qu'un bouton de validation (« Enregistrer la
carte »). `onCardValidated` ne se déclenche **que** lorsque tous les
champs passent la validation ; les champs invalides sont mis en évidence
avec des messages d'erreur traduits.

### Contrôler l'état vous-même

```kotlin
val formState = rememberOpenpayCardFormState()

OpenpayCardForm(
    state = formState,
    onCardValidated = { card -> ... },
)

// ailleurs : formState.cardNumber, formState.detectedBrand,
// formState.validate(), formState.isFieldInvalid(CardField.CARD_NUMBER)
```

Pour des raisons de sécurité, l'état du formulaire n'est **pas** conservé
après la mort du processus — les données de carte ne touchent jamais le
disque.

## Champs individuels

Chaque champ est public et peut être composé dans votre propre mise en
page :

- `OpenpayHolderNameField`
- `OpenpayCardNumberField` — formatage par groupes (4-4-4-4, Amex 4-6-5)
  et badge de marque
- `OpenpayExpirationField` — formatage visuel MM/AA sur les chiffres MMAA
- `OpenpaySecurityCodeField` — saisie masquée, longueur adaptée à la
  marque (4 pour Amex, 3 sinon)

## Interopérabilité XML

Les applications basées sur les vues obtiennent le même formulaire sans
aucun code Compose :

```xml
<mx.dev1.openpay.sdk.ui.view.OpenpayCardFormView
    android:id="@+id/openpay_card_form"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

```kotlin
findViewById<OpenpayCardFormView>(R.id.openpay_card_form).onCardValidated = { card ->
    // le tokeniser
}
```

## Accessibilité

Les composants sont conçus pour que les technologies d'assistance ne
perdent aucune information :

- Chaque champ expose son **libellé et son texte d'erreur via la
  sémantique**, si bien que TalkBack annonce « Numéro de carte, erreur,
  saisissez un numéro de carte valide ».
- Le badge de la marque détectée possède une description de contenu
  (« Marque de carte détectée : VISA »).
- Le code de sécurité est masqué visuellement **et** décrit comme masqué
  au lecteur d'écran ; le clavier bascule en `NumberPassword`.
- Les zones tactiles respectent le minimum Material de **48dp**.
- Les couleurs d'erreur proviennent des rôles d'erreur de Material 3, ce
  qui préserve le contraste dans les thèmes clair et sombre.
- Tous les textes sont traduits (voir
  [Internationalisation](internationalization.md)), y compris les
  descriptions d'accessibilité.

## Thématisation

`OpenpayTheme` applique la palette Openpay sur Material 3 avec une prise
en charge automatique des modes clair et sombre. Si votre application
définit déjà un `MaterialTheme`, retirez simplement `OpenpayTheme` et les
composants du SDK hériteront de vos couleurs, de votre typographie et de
vos formes.
