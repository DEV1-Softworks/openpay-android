# Primeiros passos

Este guia leva você do zero ao seu primeiro token de cartão. Não é necessária
experiência prévia com Android: cada passo é explicado em detalhes.

> **O que é um token?** Os dados do cartão são sensíveis, portanto seu app
> nunca os envia para os seus próprios servidores. Em vez disso, o SDK troca
> o cartão por um **token** de uso único diretamente com a Openpay. Seu
> backend usa então esse token para criar a cobrança. Seus servidores nunca
> veem o número do cartão.

## Requisitos

| Requisito | Mínimo |
|---|---|
| Android Studio | Narwhal (2025.1) ou mais recente |
| JDK | 17 (o Android Studio já inclui um) |
| Dispositivo / emulador Android | Android 8.0 (API 26) ou mais recente |
| Conta Openpay | Merchant ID + chave de API **pública** do [painel da Openpay](https://www.openpay.mx) |

## 1. Instale o SDK

Adicione a dependência ao `build.gradle.kts` do seu módulo:

```kotlin
dependencies {
    implementation("mx.dev1.openpay:openpay-sdk:1.0.0")
}
```

O SDK já declara a permissão `INTERNET` em seu manifesto; você não precisa
adicionar mais nada.

## 2. Configure o SDK

Crie uma instância de `Openpay` com as credenciais do seu comércio:

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
        environment = OpenpayEnvironment.SANDBOX, // SANDBOX durante os testes
    )
)
```

> **Nunca** distribua sua chave de API *privada* dentro do app. Apenas a
> chave pública é segura em dispositivos.

## 3. Capture o cartão e crie um token

O caminho mais rápido é o formulário Compose pronto para uso, que valida
todos os campos e entrega um objeto `Card` apenas quando os dados estão
corretos:

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

Prefere construir sua própria interface? Crie o `Card` você mesmo e
valide-o com o `CardValidator`:

```kotlin
import mx.dev1.openpay.sdk.domain.model.Card

val card = Card(
    holderName = "Juan Pérez Ramírez",
    cardNumber = "4111111111111111",
    expirationMonth = 12,
    expirationYear = 30,
    securityCode = "110",
)
val result = openpay.createToken(card) // suspend fun que retorna Result<Token>
```

Consumidores sem coroutines podem usar a variante com callback,
`openpay.createToken(card, callback)`; o callback é executado na thread
principal.

## 4. Inicie a sessão do dispositivo antifraude

O motor de risco da Openpay precisa de uma impressão digital do dispositivo
por checkout. Colete-a uma vez por tela de pagamento e envie o id retornado
ao seu backend junto com o token:

```kotlin
val deviceSessionId = openpay.setupDeviceSession(activity)
```

## 5. Trate os erros

Toda falha é uma `OpenpayException`:

| Tipo | Significado | Reação típica |
|---|---|---|
| `ValidationError` | O cartão falhou na validação local; nenhuma chamada de rede aconteceu | Destacar `validationResult.invalidFields` |
| `ServiceError` | A Openpay rejeitou a requisição (`errorCode`, `category`, `requestId`) | Mostrar uma mensagem, registrar o `requestId` |
| `ConnectionError` | A API estava inacessível | Oferecer uma nova tentativa |

## 6. Cartões de teste (sandbox)

| Número | Bandeira |
|---|---|
| 4111 1111 1111 1111 | Visa |
| 5555 5555 5555 4444 | Mastercard |
| 3782 822463 10005 | American Express |

Qualquer data de validade futura e qualquer código de segurança bem formado
funcionam no sandbox.

## Próximos passos

- [Arquitetura](architecture.md) — como o SDK é organizado internamente
- [Componentes de UI](ui-components.md) — o formulário Compose, a interoperabilidade com XML e a acessibilidade
- [Internacionalização](internationalization.md) — idiomas e substituições
- [Contribuindo](contributing.md) — como compilar e testar o projeto você mesmo
