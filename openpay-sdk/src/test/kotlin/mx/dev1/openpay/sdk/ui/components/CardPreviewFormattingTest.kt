package mx.dev1.openpay.sdk.ui.components

import mx.dev1.openpay.sdk.domain.validation.CardBrand
import org.junit.Assert.assertEquals
import org.junit.Test

class CardPreviewFormattingTest {

    @Test
    fun `empty number masks the sixteen digit silhouette in groups of four`() {
        assertEquals(
            "•••• •••• •••• ••••",
            formatPreviewCardNumber("", CardBrand.UNKNOWN),
        )
    }

    @Test
    fun `partial number keeps typed digits and masks the rest`() {
        assertEquals(
            "4111 11•• •••• ••••",
            formatPreviewCardNumber("411111", CardBrand.VISA),
        )
    }

    @Test
    fun `complete number shows every digit grouped`() {
        assertEquals(
            "4111 1111 1111 1111",
            formatPreviewCardNumber("4111111111111111", CardBrand.VISA),
        )
    }

    @Test
    fun `american express masks a fifteen digit silhouette`() {
        assertEquals(
            "3411 11•• •••• •••",
            formatPreviewCardNumber("341111", CardBrand.AMERICAN_EXPRESS),
        )
    }

    @Test
    fun `digits beyond the expected length are ignored`() {
        assertEquals(
            "4111 1111 1111 1111",
            formatPreviewCardNumber("41111111111111119999", CardBrand.VISA),
        )
    }

    @Test
    fun `empty expiration masks both parts`() {
        assertEquals("••/••", formatPreviewExpiration(""))
    }

    @Test
    fun `partial expiration masks only the missing positions`() {
        assertEquals("1•/••", formatPreviewExpiration("1"))
        assertEquals("12/••", formatPreviewExpiration("12"))
        assertEquals("12/3•", formatPreviewExpiration("123"))
    }

    @Test
    fun `complete expiration shows month and year`() {
        assertEquals("12/30", formatPreviewExpiration("1230"))
    }
}
