package mx.dev1.openpay.sdk.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme

@Preview(name = "Holder name field", showBackground = true)
@Composable
private fun HolderNameFieldPreview() {
    OpenpayTheme {
        OpenpayHolderNameField(
            value = "Juan Pérez Ramírez",
            onValueChange = {},
            isError = false,
        )
    }
}

@Preview(name = "Holder name field – error", showBackground = true)
@Composable
private fun HolderNameFieldErrorPreview() {
    OpenpayTheme {
        OpenpayHolderNameField(
            value = "",
            onValueChange = {},
            isError = true,
        )
    }
}

@Preview(name = "Card number field", showBackground = true)
@Composable
private fun CardNumberFieldPreview() {
    OpenpayTheme {
        OpenpayCardNumberField(
            value = "4111111111111111",
            onValueChange = {},
            isError = false,
        )
    }
}

@Preview(name = "Card number field – error", showBackground = true)
@Composable
private fun CardNumberFieldErrorPreview() {
    OpenpayTheme {
        OpenpayCardNumberField(
            value = "1234",
            onValueChange = {},
            isError = true,
        )
    }
}

@Preview(name = "Expiration field", showBackground = true)
@Composable
private fun ExpirationFieldPreview() {
    OpenpayTheme {
        OpenpayExpirationField(
            value = "1230",
            onValueChange = {},
            isError = false,
        )
    }
}

@Preview(name = "Security code field", showBackground = true)
@Composable
private fun SecurityCodeFieldPreview() {
    OpenpayTheme {
        OpenpaySecurityCodeField(
            value = "123",
            onValueChange = {},
            isError = false,
        )
    }
}
