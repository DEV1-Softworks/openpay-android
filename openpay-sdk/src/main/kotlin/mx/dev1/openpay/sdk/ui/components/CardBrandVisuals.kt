package mx.dev1.openpay.sdk.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import mx.dev1.openpay.sdk.R
import mx.dev1.openpay.sdk.domain.validation.CardBrand

private val VisaNavyBlue = Color(0xFF1A1F71)
private val VisaYellow = Color(0xFFF7B600)
private val MastercardOrange = Color(0xFFFF5F00)
private val MastercardYellow = Color(0xFFF79E1B)
private val AmericanExpressBlue = Color(0xFF006FCF)
private val UnknownBrandGradientStart = Color(0xFF8E9199)
private val UnknownBrandGradientEnd = Color(0xFF4A4D55)

/**
 * Visual identity of a card brand: the logo asset, how the logo must be
 * announced to screen readers, and the card background colors (one color
 * renders as a solid fill, two as a linear gradient).
 */
internal data class CardBrandVisualStyle(
    @param:DrawableRes val logoDrawableResId: Int?,
    val logoDescription: String?,
    val backgroundColors: List<Color>,
)

/**
 * Resolves the visual identity used for [brand] by the card preview and the
 * brand logo badge. Unknown brands keep the neutral gray card and no logo.
 */
internal fun cardBrandVisualStyle(brand: CardBrand): CardBrandVisualStyle =
    when (brand) {
        CardBrand.VISA -> CardBrandVisualStyle(
            logoDrawableResId = R.drawable.visa,
            logoDescription = "Visa",
            backgroundColors = listOf(VisaNavyBlue, VisaYellow),
        )
        CardBrand.MASTERCARD -> CardBrandVisualStyle(
            logoDrawableResId = R.drawable.mastercard,
            logoDescription = "Mastercard",
            backgroundColors = listOf(MastercardOrange, MastercardYellow),
        )
        CardBrand.AMERICAN_EXPRESS -> CardBrandVisualStyle(
            logoDrawableResId = R.drawable.amex,
            logoDescription = "American Express",
            backgroundColors = listOf(AmericanExpressBlue),
        )
        CardBrand.UNKNOWN -> CardBrandVisualStyle(
            logoDrawableResId = null,
            logoDescription = null,
            backgroundColors = listOf(UnknownBrandGradientStart, UnknownBrandGradientEnd),
        )
    }

/**
 * Builds the card background for this style: a solid fill when it has one
 * color, a linear gradient when it has two or more.
 */
internal fun CardBrandVisualStyle.backgroundBrush(): Brush =
    if (backgroundColors.size == 1) {
        SolidColor(backgroundColors.single())
    } else {
        Brush.linearGradient(backgroundColors)
    }

/**
 * Badge with the brand logo over its brand color, meant for token or charge
 * outputs where the brand comes back from the API. Renders nothing when the
 * brand is [CardBrand.UNKNOWN].
 *
 * Accessibility: the badge announces the brand name (for example "Visa"),
 * so screen reader users get the same information the logo conveys.
 *
 * @param brand detected or API-reported brand, see [CardBrand.fromBrandName].
 */
@Composable
fun OpenpayCardBrandLogo(
    brand: CardBrand,
    modifier: Modifier = Modifier,
) {
    val style = cardBrandVisualStyle(brand)
    val logoDrawableResId = style.logoDrawableResId ?: return

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(style.backgroundBrush())
            .testTag(OpenpayFormTags.CARD_BRAND_LOGO)
            .semantics { contentDescription = style.logoDescription.orEmpty() },
    ) {
        Image(
            painter = painterResource(logoDrawableResId),
            contentDescription = null,
            modifier = Modifier
                .height(28.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
