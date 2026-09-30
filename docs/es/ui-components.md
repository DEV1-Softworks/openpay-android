# Componentes de UI

El SDK incluye un kit de captura de tarjeta con enfoque Compose-first.
Puedes usar el formulario completo, campos individuales, o envolver el
formulario dentro de un layout XML.

## El formulario completo

```kotlin
import mx.dev1.openpay.sdk.ui.components.OpenpayCardForm
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme

OpenpayTheme {                       // opcional: hereda tu propio MaterialTheme en su lugar
    OpenpayCardForm(
        onCardValidated = { card -> /* tokenízala */ },
    )
}
```

El formulario contiene el nombre del titular, el número de tarjeta (con
detección de marca en vivo), la fecha de vencimiento MM/AA y el código de
seguridad enmascarado, además de un botón de envío. `onCardValidated` se
dispara **solo** cuando todos los campos pasan la validación; los campos
inválidos se resaltan con mensajes de error traducidos.

### Controlar el estado tú mismo

```kotlin
val formState = rememberOpenpayCardFormState()

OpenpayCardForm(
    state = formState,
    onCardValidated = { card -> ... },
)

// en otro lugar: formState.cardNumber, formState.detectedBrand,
// formState.validate(), formState.isFieldInvalid(CardField.CARD_NUMBER)
```

Por seguridad, el estado del formulario **no** se persiste al morir el
proceso: los datos de la tarjeta nunca tocan el disco.

## Campos individuales

Cada campo es público y puede componerse dentro de tu propio layout:

- `OpenpayHolderNameField`
- `OpenpayCardNumberField` — formato agrupado (4-4-4-4, Amex 4-6-5) y una
  insignia de marca
- `OpenpayExpirationField` — formato visual MM/AA sobre los dígitos MMAA
- `OpenpaySecurityCodeField` — entrada enmascarada, longitud según la marca
  (4 para Amex, 3 en los demás casos)

## Identidad de marca en la vista previa

La vista previa de la tarjeta reacciona a la marca detectada:

| Marca | Fondo de la tarjeta | Logo |
|---|---|---|
| Visa | Degradado azul marino → amarillo | Logotipo de Visa |
| Mastercard | Degradado naranja → amarillo | Círculos de Mastercard |
| American Express | Azul sólido | Logo de Amex |
| Desconocida | Degradado gris neutro | ninguno |

El logo se muestra en la esquina superior derecha de la vista previa y
aparece en cuanto el prefijo del número identifica la marca.

Para mostrar el mismo logo junto al resultado de la tokenización (la API
devuelve la marca como cadena, por ejemplo `"visa"`), usa el badge público:

```kotlin
import mx.dev1.openpay.sdk.domain.validation.CardBrand
import mx.dev1.openpay.sdk.ui.components.OpenpayCardBrandLogo

OpenpayCardBrandLogo(brand = CardBrand.fromBrandName(token.card?.brand))
```

El badge dibuja el logo blanco sobre el color de su marca, no muestra nada
para marcas desconocidas y anuncia el nombre de la marca ("Visa",
"Mastercard", "American Express") a los lectores de pantalla.

## Interoperabilidad con XML

Las apps basadas en vistas obtienen el mismo formulario sin nada de código
Compose:

```xml
<mx.dev1.openpay.sdk.ui.view.OpenpayCardFormView
    android:id="@+id/openpay_card_form"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

```kotlin
findViewById<OpenpayCardFormView>(R.id.openpay_card_form).onCardValidated = { card ->
    // tokenízala
}
```

## Accesibilidad

Los componentes están diseñados para que las tecnologías de asistencia no
pierdan nada:

- Cada campo expone su **etiqueta y texto de error mediante semántica**,
  por lo que TalkBack anuncia "Número de tarjeta, error, ingresa un número
  de tarjeta válido".
- La insignia de la marca detectada tiene una descripción de contenido
  ("Marca de tarjeta detectada: VISA").
- El código de seguridad se enmascara visualmente **y** se describe como
  oculto al lector de pantalla; el teclado cambia a `NumberPassword`.
- Los objetivos táctiles mantienen el mínimo de Material de **48dp**.
- Los colores de error provienen de los roles de error de Material 3,
  preservando el contraste en los temas claro y oscuro.
- Todos los textos están traducidos (ver
  [Internacionalización](internationalization.md)), incluidas las
  descripciones de accesibilidad.

## Temas

`OpenpayTheme` aplica la paleta de Openpay sobre Material 3 con soporte
automático de tema claro/oscuro. Si tu app ya define un `MaterialTheme`,
simplemente omite `OpenpayTheme` y los componentes del SDK heredan tus
colores, tipografía y formas.
