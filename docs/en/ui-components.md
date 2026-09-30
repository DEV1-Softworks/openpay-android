# UI components

The SDK ships a Compose-first card capture kit. You can use the whole form,
individual fields, or wrap the form inside an XML layout.

## The complete form

```kotlin
import mx.dev1.openpay.sdk.ui.components.OpenpayCardForm
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme

OpenpayTheme {                       // optional: inherit your own MaterialTheme instead
    OpenpayCardForm(
        onCardValidated = { card -> /* tokenize it */ },
    )
}
```

The form contains the cardholder name, card number (with live brand
detection), MM/YY expiration and masked security code, plus a submit
button. `onCardValidated` fires **only** when every field passes
validation; invalid fields are highlighted with translated error messages.

### Implementing with the native form

The callback hands you a validated `Card`; tokenize it and send the token
id to your backend:

```kotlin
val scope = rememberCoroutineScope()

OpenpayCardForm(
    onCardValidated = { card ->
        scope.launch {
            openpay.createToken(card)
                .onSuccess { token -> /* send token.tokenId to your backend */ }
                .onFailure { error -> /* show the error */ }
        }
    },
)
```

See [Getting started](getting-started.md) for how to build the `openpay`
instance with your merchant id and public key.

### Controlling the state yourself

```kotlin
val formState = rememberOpenpayCardFormState()

OpenpayCardForm(
    state = formState,
    onCardValidated = { card -> ... },
)

// elsewhere: formState.cardNumber, formState.detectedBrand,
// formState.validate(), formState.isFieldInvalid(CardField.CARD_NUMBER)
```

For security, the form state is **not** persisted across process death —
card data never touches disk.

## Implementing with custom inputs

Each field is public and can be composed into your own layout while
keeping the SDK's formatting and validation:

- `OpenpayHolderNameField`
- `OpenpayCardNumberField` — grouped formatting (4-4-4-4, Amex 4-6-5)
- `OpenpayExpirationField` — MM/YY visual formatting over MMYY digits
- `OpenpaySecurityCodeField` — masked input, brand-aware length (4 for
  Amex, 3 otherwise)

Wire them to `rememberOpenpayCardFormState()`, which keeps digits-only
values, detects the brand and validates every field:

```kotlin
val formState = rememberOpenpayCardFormState()

Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    OpenpayCardPreview(              // optional: brand colors and logo included
        holderName = formState.holderName,
        cardNumber = formState.cardNumber,
        expiration = formState.expiration,
    )
    OpenpayHolderNameField(
        value = formState.holderName,
        onValueChange = formState::updateHolderName,
        isError = formState.isFieldInvalid(CardField.HOLDER_NAME),
    )
    OpenpayCardNumberField(
        value = formState.cardNumber,
        onValueChange = formState::updateCardNumber,
        isError = formState.isFieldInvalid(CardField.CARD_NUMBER),
    )
    OpenpayExpirationField(
        value = formState.expiration,
        onValueChange = formState::updateExpiration,
        isError = formState.isFieldInvalid(CardField.EXPIRATION),
    )
    OpenpaySecurityCodeField(
        value = formState.securityCode,
        onValueChange = formState::updateSecurityCode,
        isError = formState.isFieldInvalid(CardField.SECURITY_CODE),
    )
    Button(onClick = {
        formState.validate()?.let { card -> /* createToken(card) as above */ }
    }) {
        Text("Pay")
    }
}
```

`formState.validate()` returns the `Card` ready for tokenization when
every field is valid; otherwise it returns null and flags the failing
fields so `isFieldInvalid` highlights them.

## Brand identity on the card preview

The live card preview reacts to the detected brand:

| Brand | Card background | Logo |
|---|---|---|
| Visa | Navy blue → yellow gradient (70% navy) | Visa wordmark |
| Mastercard | Orange → yellow gradient | Mastercard circles |
| American Express | Solid blue | Amex logo |
| Unknown | Neutral gray gradient | none |

The logo sits at the top-right corner of the preview and appears as soon
as the number prefix identifies the brand.

To show the same logo next to a tokenization result (the API echoes the
brand as a string such as `"visa"`), use the public badge:

```kotlin
import mx.dev1.openpay.sdk.domain.validation.CardBrand
import mx.dev1.openpay.sdk.ui.components.OpenpayCardBrandLogo

OpenpayCardBrandLogo(brand = CardBrand.fromBrandName(token.card?.brand))
```

The badge draws the white logo over its brand color, renders nothing for
unknown brands, and announces the brand name ("Visa", "Mastercard",
"American Express") to screen readers.

## XML interop

View-based apps get the same form without any Compose code:

```xml
<mx.dev1.openpay.sdk.ui.view.OpenpayCardFormView
    android:id="@+id/openpay_card_form"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

```kotlin
findViewById<OpenpayCardFormView>(R.id.openpay_card_form).onCardValidated = { card ->
    // tokenize it
}
```

## Accessibility

The components are designed so assistive technologies lose nothing:

- Every field exposes its **label and error text through semantics**, so
  TalkBack announces "Card number, error, enter a valid card number".
- The detected brand badge has a content description ("Detected card
  brand: VISA").
- The security code is masked visually **and** described as hidden to the
  screen reader; the keyboard switches to `NumberPassword`.
- Touch targets keep the Material minimum of **48dp**.
- Error colors come from the Material 3 error roles, preserving contrast
  in light and dark themes.
- All texts are translated (see
  [Internationalization](internationalization.md)), including the
  accessibility descriptions.

## Theming

`OpenpayTheme` applies the Openpay palette on Material 3 with automatic
light/dark support. If your app already defines a `MaterialTheme`, simply
drop `OpenpayTheme` and the SDK components inherit your colors, typography
and shapes.
