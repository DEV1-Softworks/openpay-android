package mx.dev1.openpay.sdk.ui.input

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Shows the expiration as MM/YY while the editable text stays the four
 * digits MMYY.
 */
class ExpirationVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val showsSeparator = digits.length > 2

        val formattedExpiration =
            if (showsSeparator) "${digits.take(2)}/${digits.drop(2)}" else digits

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                if (showsSeparator && offset > 2) offset + 1 else offset

            override fun transformedToOriginal(offset: Int): Int =
                if (showsSeparator && offset > 2) (offset - 1).coerceAtMost(digits.length) else offset
        }

        return TransformedText(AnnotatedString(formattedExpiration), offsetMapping)
    }
}
