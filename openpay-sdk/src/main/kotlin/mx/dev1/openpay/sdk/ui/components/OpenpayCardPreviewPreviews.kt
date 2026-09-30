package mx.dev1.openpay.sdk.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme

@Preview(name = "Card preview – empty", showBackground = true)
@Composable
private fun EmptyCardPreviewPreview() {
    OpenpayTheme {
        OpenpayCardPreview(
            holderName = "",
            cardNumber = "",
            expiration = "",
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "Card preview – Visa in progress", showBackground = true)
@Composable
private fun PartialCardPreviewPreview() {
    OpenpayTheme {
        OpenpayCardPreview(
            holderName = "Juan Pérez",
            cardNumber = "41111111",
            expiration = "12",
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "Card preview – Amex complete", showBackground = true)
@Composable
private fun AmexCardPreviewPreview() {
    OpenpayTheme {
        OpenpayCardPreview(
            holderName = "María López",
            cardNumber = "341111111111111",
            expiration = "1230",
            modifier = Modifier.padding(16.dp),
        )
    }
}
