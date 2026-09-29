# Componentes de UI

O SDK inclui um kit de captura de cartão feito primeiro para Compose. Você
pode usar o formulário completo, campos individuais ou envolver o formulário
em um layout XML.

## O formulário completo

```kotlin
import mx.dev1.openpay.sdk.ui.components.OpenpayCardForm
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme

OpenpayTheme {                       // opcional: herde seu próprio MaterialTheme em vez disso
    OpenpayCardForm(
        onCardValidated = { card -> /* tokenize-o */ },
    )
}
```

O formulário contém o nome do titular, o número do cartão (com detecção de
bandeira em tempo real), a validade MM/AA e o código de segurança mascarado,
além de um botão de envio. `onCardValidated` é disparado **apenas** quando
todos os campos passam na validação; campos inválidos são destacados com
mensagens de erro traduzidas.

### Controlando o estado você mesmo

```kotlin
val formState = rememberOpenpayCardFormState()

OpenpayCardForm(
    state = formState,
    onCardValidated = { card -> ... },
)

// em outro lugar: formState.cardNumber, formState.detectedBrand,
// formState.validate(), formState.isFieldInvalid(CardField.CARD_NUMBER)
```

Por segurança, o estado do formulário **não** é persistido quando o processo
é encerrado — os dados do cartão nunca tocam o disco.

## Campos individuais

Cada campo é público e pode ser composto no seu próprio layout:

- `OpenpayHolderNameField`
- `OpenpayCardNumberField` — formatação em grupos (4-4-4-4, Amex 4-6-5) e um
  selo da bandeira
- `OpenpayExpirationField` — formatação visual MM/AA sobre os dígitos MMAA
- `OpenpaySecurityCodeField` — entrada mascarada, comprimento conforme a
  bandeira (4 para Amex, 3 nos demais casos)

## Interoperabilidade com XML

Apps baseados em views obtêm o mesmo formulário sem nenhum código Compose:

```xml
<mx.dev1.openpay.sdk.ui.view.OpenpayCardFormView
    android:id="@+id/openpay_card_form"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

```kotlin
findViewById<OpenpayCardFormView>(R.id.openpay_card_form).onCardValidated = { card ->
    // tokenize-o
}
```

## Acessibilidade

Os componentes são projetados para que as tecnologias assistivas não percam
nada:

- Cada campo expõe seu **rótulo e texto de erro por meio da semântica**, de
  modo que o TalkBack anuncia "Número do cartão, erro, insira um número de
  cartão válido".
- O selo da bandeira detectada tem uma descrição de conteúdo ("Marca do
  cartão detectada: VISA").
- O código de segurança é mascarado visualmente **e** descrito como oculto
  para o leitor de tela; o teclado muda para `NumberPassword`.
- As áreas de toque respeitam o mínimo de **48dp** do Material.
- As cores de erro vêm dos papéis de erro do Material 3, preservando o
  contraste nos temas claro e escuro.
- Todos os textos são traduzidos (veja
  [Internacionalização](internationalization.md)), incluindo as descrições
  de acessibilidade.

## Temas

`OpenpayTheme` aplica a paleta da Openpay sobre o Material 3 com suporte
automático a temas claro e escuro. Se o seu app já define um
`MaterialTheme`, basta omitir o `OpenpayTheme` e os componentes do SDK
herdarão suas cores, tipografia e formas.
