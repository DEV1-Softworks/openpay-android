package mx.dev1.openpay.sdk.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.dev1.openpay.sdk.R
import mx.dev1.openpay.sdk.domain.validation.CardBrand
import mx.dev1.openpay.sdk.i18n.OpenpayLocalized

private val CardPreviewTextColor = Color.White
private val CardPreviewLabelColor = Color(0xFFD6D8DC)

private val BrandLogoSlotHeight = 28.dp

private const val MASK_CHARACTER = '•'
private const val AMERICAN_EXPRESS_NUMBER_LENGTH = 15
private const val DEFAULT_NUMBER_LENGTH = 16
private const val NUMBER_GROUP_SIZE = 4
private const val EXPIRATION_PART_LENGTH = 2

/**
 * Groups the typed digits and masks the missing positions with bullets, so
 * the preview always shows the full card number silhouette. American Express
 * numbers mask to 15 digits, every other brand to 16.
 */
internal fun formatPreviewCardNumber(cardNumberDigits: String, brand: CardBrand): String {
    val expectedLength = if (brand == CardBrand.AMERICAN_EXPRESS) {
        AMERICAN_EXPRESS_NUMBER_LENGTH
    } else {
        DEFAULT_NUMBER_LENGTH
    }
    val paddedDigits = cardNumberDigits
        .take(expectedLength)
        .padEnd(expectedLength, MASK_CHARACTER)
    return paddedDigits.chunked(NUMBER_GROUP_SIZE).joinToString(separator = " ")
}

/**
 * Formats MMYY digits as MM/YY, masking the missing positions with bullets
 * (for example "1" becomes "1•/••").
 */
internal fun formatPreviewExpiration(expirationDigits: String): String {
    val paddedDigits = expirationDigits
        .take(EXPIRATION_PART_LENGTH * 2)
        .padEnd(EXPIRATION_PART_LENGTH * 2, MASK_CHARACTER)
    val month = paddedDigits.take(EXPIRATION_PART_LENGTH)
    val year = paddedDigits.drop(EXPIRATION_PART_LENGTH)
    return "$month/$year"
}

/**
 * Visual representation of the card being captured, mirroring the form input
 * in real time: masked number, holder name and expiration date, plus the
 * detected brand.
 *
 * Accessibility: the preview only mirrors what the form fields already
 * announce, so it exposes a single short content description and hides its
 * inner texts from screen readers. The card number is never read aloud.
 *
 * @param holderName raw holder name as typed in the form.
 * @param cardNumber card number digits, without separators.
 * @param expiration expiration digits in MMYY order.
 */
@Composable
fun OpenpayCardPreview(
    holderName: String,
    cardNumber: String,
    expiration: String,
    modifier: Modifier = Modifier,
) = OpenpayLocalized {
    val previewDescription = stringResource(R.string.openpay_card_preview_description)
    val brand = CardBrand.fromCardNumber(cardNumber)
    val brandStyle = cardBrandVisualStyle(brand)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 1.8f)
            .clip(RoundedCornerShape(20.dp))
            .background(brandStyle.backgroundBrush())
            .testTag(OpenpayFormTags.CARD_PREVIEW)
            .clearAndSetSemantics { contentDescription = previewDescription },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Fixed-height slot so the layout does not jump when the brand
            // becomes known and the logo appears.
            Box(
                modifier = Modifier
                    .height(BrandLogoSlotHeight)
                    .align(Alignment.End),
            ) {
                brandStyle.logoDrawableResId?.let { logoDrawableResId ->
                    Image(
                        painter = painterResource(logoDrawableResId),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxHeight()
                            .testTag(OpenpayFormTags.CARD_PREVIEW_BRAND_LOGO),
                    )
                }
            }

            Text(
                text = formatPreviewCardNumber(cardNumber, brand),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = CardPreviewTextColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.openpay_card_preview_holder_label),
                        color = CardPreviewLabelColor,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = holderName.trim().uppercase().ifEmpty {
                            stringResource(R.string.openpay_card_preview_holder_placeholder)
                        },
                        color = CardPreviewTextColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.openpay_card_preview_expiration_label),
                        color = CardPreviewLabelColor,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = formatPreviewExpiration(expiration),
                        color = CardPreviewTextColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
