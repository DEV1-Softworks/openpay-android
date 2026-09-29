package mx.dev1.openpay.sdk.ui.input

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import mx.dev1.openpay.sdk.domain.validation.CardBrand

/**
 * Formats card numbers with the spacing users see on physical cards:
 * 4-6-5 for American Express and groups of four for every other brand.
 * The underlying editable text stays digits-only.
 */
class CardNumberVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text

        // Digit counts after which a space is shown, e.g. [4, 8, 12] for 16
        // digit cards or [4, 10] for American Express. Boundaries at or past
        // the end of the input insert nothing.
        val groupBoundaries = groupSizesFor(CardBrand.fromCardNumber(digits))
            .runningReduce(Int::plus)
            .dropLast(1)
            .filter { boundary -> boundary < digits.length }

        val formattedNumber = buildString {
            digits.forEachIndexed { index, digit ->
                append(digit)
                if (index + 1 in groupBoundaries) append(' ')
            }
        }

        // Index every space occupies inside the formatted text.
        val spacePositions = groupBoundaries.mapIndexed { spaceIndex, boundary ->
            boundary + spaceIndex
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                offset + groupBoundaries.count { boundary -> boundary <= offset }

            override fun transformedToOriginal(offset: Int): Int =
                (offset - spacePositions.count { spacePosition -> spacePosition < offset })
                    .coerceIn(0, digits.length)
        }

        return TransformedText(AnnotatedString(formattedNumber), offsetMapping)
    }

    private fun groupSizesFor(brand: CardBrand): List<Int> =
        when (brand) {
            CardBrand.AMERICAN_EXPRESS -> listOf(4, 6, 5)
            else -> listOf(4, 4, 4, 4, 3)
        }
}
