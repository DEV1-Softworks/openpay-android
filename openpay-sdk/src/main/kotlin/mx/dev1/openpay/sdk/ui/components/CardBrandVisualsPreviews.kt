package mx.dev1.openpay.sdk.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.dev1.openpay.sdk.domain.validation.CardBrand
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme

@Preview(name = "Brand logos", showBackground = true)
@Composable
private fun CardBrandLogosPreview() {
    OpenpayTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OpenpayCardBrandLogo(brand = CardBrand.VISA)
            OpenpayCardBrandLogo(brand = CardBrand.MASTERCARD)
            OpenpayCardBrandLogo(brand = CardBrand.AMERICAN_EXPRESS)
        }
    }
}
