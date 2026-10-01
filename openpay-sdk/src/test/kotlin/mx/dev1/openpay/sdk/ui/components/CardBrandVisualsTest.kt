package mx.dev1.openpay.sdk.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import mx.dev1.openpay.sdk.R
import mx.dev1.openpay.sdk.domain.validation.CardBrand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CardBrandVisualsTest {

    @Test
    fun `visa uses its logo over a navy blue to yellow gradient`() {
        val style = cardBrandVisualStyle(CardBrand.VISA)

        assertEquals(R.drawable.visa, style.logoDrawableResId)
        assertEquals("Visa", style.logoDescription)
        assertEquals(
            listOf(
                0.0f to Color(0xFF1A1F71),
                0.7f to Color(0xFF1A1F71),
                1.0f to Color(0xFFF7B600),
            ),
            style.backgroundColorStops,
        )
    }

    @Test
    fun `visa keeps navy blue over at least seventy percent of the card`() {
        val stops = cardBrandVisualStyle(CardBrand.VISA).backgroundColorStops
        val navyBlue = Color(0xFF1A1F71)
        val lastNavyBlueStop = stops.last { stop -> stop.second == navyBlue }.first

        assertTrue(lastNavyBlueStop >= 0.7f)
    }

    @Test
    fun `mastercard uses its logo over an orange to yellow gradient`() {
        val style = cardBrandVisualStyle(CardBrand.MASTERCARD)

        assertEquals(R.drawable.mastercard, style.logoDrawableResId)
        assertEquals("Mastercard", style.logoDescription)
        assertEquals(
            listOf(
                0.0f to Color(0xFFFF5F00),
                1.0f to Color(0xFFF79E1B),
            ),
            style.backgroundColorStops,
        )
    }

    @Test
    fun `american express uses its logo over a solid blue`() {
        val style = cardBrandVisualStyle(CardBrand.AMERICAN_EXPRESS)

        assertEquals(R.drawable.amex, style.logoDrawableResId)
        assertEquals("American Express", style.logoDescription)
        assertEquals(listOf(0.0f to Color(0xFF006FCF)), style.backgroundColorStops)
    }

    @Test
    fun `unknown brands keep the neutral gray gradient and no logo`() {
        val style = cardBrandVisualStyle(CardBrand.UNKNOWN)

        assertNull(style.logoDrawableResId)
        assertNull(style.logoDescription)
        assertEquals(
            listOf(
                0.0f to Color(0xFF8E9199),
                1.0f to Color(0xFF4A4D55),
            ),
            style.backgroundColorStops,
        )
    }

    @Test
    fun `a single background color renders as a solid brush`() {
        val brush = cardBrandVisualStyle(CardBrand.AMERICAN_EXPRESS).backgroundBrush()

        assertEquals(SolidColor(Color(0xFF006FCF)), brush)
    }

    @Test
    fun `several background colors render as a gradient brush`() {
        val brush = cardBrandVisualStyle(CardBrand.VISA).backgroundBrush()

        assertTrue(brush !is SolidColor)
    }
}
