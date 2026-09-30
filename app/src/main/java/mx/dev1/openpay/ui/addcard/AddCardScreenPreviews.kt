package mx.dev1.openpay.ui.addcard

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import mx.dev1.openpay.sdk.domain.model.Token
import mx.dev1.openpay.sdk.domain.model.TokenizedCard
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme
import mx.dev1.openpay.ui.checkout.CheckoutUiState

@Preview(name = "Add card – empty form", showBackground = true)
@Composable
private fun EmptyAddCardScreenPreview() {
    OpenpayTheme {
        AddCardScreenContent(
            uiState = CheckoutUiState(),
            onCardValidated = {},
            onNavigateBack = {},
        )
    }
}

@Preview(name = "Add card – token created", showBackground = true)
@Composable
private fun TokenCreatedAddCardScreenPreview() {
    OpenpayTheme {
        AddCardScreenContent(
            uiState = CheckoutUiState(
                createdToken = Token(
                    tokenId = "k1nsxpxqhyhnbjr3qfhr",
                    card = TokenizedCard(
                        maskedCardNumber = "411111XXXXXX1111",
                        holderName = "Juan Pérez Ramírez",
                        expirationMonth = "12",
                        expirationYear = "30",
                        brand = "visa",
                        cardType = "debit",
                        bankName = null,
                        bankCode = null,
                        allowsCharges = true,
                        allowsPayouts = false,
                        address = null,
                    ),
                ),
            ),
            onCardValidated = {},
            onNavigateBack = {},
        )
    }
}

@Preview(name = "Add card – tokenization error", showBackground = true)
@Composable
private fun ErrorAddCardScreenPreview() {
    OpenpayTheme {
        AddCardScreenContent(
            uiState = CheckoutUiState(
                errorMessage = "Openpay rejected the request (3001): the card was declined",
            ),
            onCardValidated = {},
            onNavigateBack = {},
        )
    }
}
