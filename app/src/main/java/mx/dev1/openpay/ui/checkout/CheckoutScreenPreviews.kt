package mx.dev1.openpay.ui.checkout

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import mx.dev1.openpay.sdk.core.OpenpayCountry
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme

@Preview(name = "Checkout – empty configuration", showBackground = true)
@Composable
private fun EmptyCheckoutScreenPreview() {
    OpenpayTheme {
        CheckoutScreenContent(
            uiState = CheckoutUiState(),
            onMerchantIdChange = {},
            onPublicApiKeyChange = {},
            onCountryChange = {},
            onProductionModeChange = {},
            onLanguageChange = {},
            onStartDeviceSession = {},
            onAddCardClick = {},
        )
    }
}

@Preview(name = "Checkout – configured with device session", showBackground = true)
@Composable
private fun ConfiguredCheckoutScreenPreview() {
    OpenpayTheme {
        CheckoutScreenContent(
            uiState = CheckoutUiState(
                merchantId = "mzdtln0bmtms6o3kck8f",
                publicApiKey = "pk_f0660ad5a39f4912872e24a7a660370c",
                country = OpenpayCountry.MEXICO,
                deviceSessionId = "8d467eee6c8c14bd8d769b8c1c395d5a",
            ),
            onMerchantIdChange = {},
            onPublicApiKeyChange = {},
            onCountryChange = {},
            onProductionModeChange = {},
            onLanguageChange = {},
            onStartDeviceSession = {},
            onAddCardClick = {},
        )
    }
}
