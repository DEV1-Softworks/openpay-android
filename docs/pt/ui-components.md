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

### Implementação com o formulário nativo

O callback entrega um `Card` validado; tokenize-o e envie o id do token
para o seu backend:

```kotlin
val scope = rememberCoroutineScope()

OpenpayCardForm(
    onCardValidated = { card ->
        scope.launch {
            openpay.createToken(card)
                .onSuccess { token -> /* envie token.tokenId ao seu backend */ }
                .onFailure { error -> /* mostre o erro */ }
        }
    },
)
```

Veja [Primeiros passos](getting-started.md) para construir a instância
`openpay` com o seu id de comerciante e chave pública.

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

## Implementação com inputs personalizados

Cada campo é público e pode ser composto no seu próprio layout mantendo a
formatação e a validação do SDK:

- `OpenpayHolderNameField`
- `OpenpayCardNumberField` — formatação em grupos (4-4-4-4, Amex 4-6-5)
- `OpenpayExpirationField` — formatação visual MM/AA sobre os dígitos MMAA
- `OpenpaySecurityCodeField` — entrada mascarada, comprimento conforme a
  bandeira (4 para Amex, 3 nos demais casos)

Conecte-os ao `rememberOpenpayCardFormState()`, que guarda apenas dígitos,
detecta a bandeira e valida todos os campos:

```kotlin
val formState = rememberOpenpayCardFormState()

Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    OpenpayCardPreview(              // opcional: inclui cores e logo da bandeira
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
        formState.validate()?.let { card -> /* createToken(card) como acima */ }
    }) {
        Text("Pagar")
    }
}
```

`formState.validate()` retorna o `Card` pronto para tokenização quando
todos os campos são válidos; caso contrário retorna null e marca os campos
com falha para que `isFieldInvalid` os destaque.

## Identidade de marca na pré-visualização

A pré-visualização do cartão reage à bandeira detectada:

| Bandeira | Fundo do cartão | Logo |
|---|---|---|
| Visa | Gradiente azul-marinho → amarelo (70% azul) | Logotipo da Visa |
| Mastercard | Gradiente laranja → amarelo | Círculos da Mastercard |
| American Express | Azul sólido | Logo da Amex |
| Desconhecida | Gradiente cinza neutro | nenhum |

O logo fica no canto superior direito da pré-visualização e aparece assim
que o prefixo do número identifica a bandeira.

Para mostrar o mesmo logo ao lado do resultado da tokenização (a API
devolve a bandeira como string, por exemplo `"visa"`), use o badge público:

```kotlin
import mx.dev1.openpay.sdk.domain.validation.CardBrand
import mx.dev1.openpay.sdk.ui.components.OpenpayCardBrandLogo

OpenpayCardBrandLogo(brand = CardBrand.fromBrandName(token.card?.brand))
```

O badge desenha o logo branco sobre a cor da sua bandeira, não mostra nada
para bandeiras desconhecidas e anuncia o nome da bandeira ("Visa",
"Mastercard", "American Express") aos leitores de tela.

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
