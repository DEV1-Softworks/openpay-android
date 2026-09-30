package mx.dev1.openpay.sdk.ui.components

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import mx.dev1.openpay.sdk.domain.validation.CardBrand
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OpenpayCardBrandLogoComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun renderLogo(brand: CardBrand) {
        composeRule.setContent {
            OpenpayTheme {
                OpenpayCardBrandLogo(brand = brand)
            }
        }
    }

    @Test
    fun `visa logo announces the brand name`() {
        renderLogo(CardBrand.VISA)

        composeRule.onNodeWithTag(OpenpayFormTags.CARD_BRAND_LOGO)
            .assertContentDescriptionEquals("Visa")
    }

    @Test
    fun `mastercard logo announces the brand name`() {
        renderLogo(CardBrand.MASTERCARD)

        composeRule.onNodeWithTag(OpenpayFormTags.CARD_BRAND_LOGO)
            .assertContentDescriptionEquals("Mastercard")
    }

    @Test
    fun `american express logo announces the brand name`() {
        renderLogo(CardBrand.AMERICAN_EXPRESS)

        composeRule.onNodeWithTag(OpenpayFormTags.CARD_BRAND_LOGO)
            .assertContentDescriptionEquals("American Express")
    }

    @Test
    fun `an unknown brand renders nothing`() {
        renderLogo(CardBrand.UNKNOWN)

        composeRule.onNodeWithTag(OpenpayFormTags.CARD_BRAND_LOGO).assertDoesNotExist()
    }
}