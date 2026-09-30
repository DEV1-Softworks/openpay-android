package mx.dev1.openpay.sdk.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme

@Preview(name = "Card form", showBackground = true)
@Preview(
    name = "Card form – dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun CardFormPreview() {
    OpenpayTheme {
        Surface {
            OpenpayCardForm(
                onCardValidated = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview(name = "Card form – without card preview", showBackground = true)
@Composable
private fun CardFormWithoutCardPreviewPreview() {
    OpenpayTheme {
        Surface {
            OpenpayCardForm(
                onCardValidated = {},
                modifier = Modifier.padding(16.dp),
                showCardPreview = false,
            )
        }
    }
}
