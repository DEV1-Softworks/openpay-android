package mx.dev1.openpay.sdk.ui.components

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OpenpayCardPreviewComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun renderPreview(
        holderName: String = "",
        cardNumber: String = "",
        expiration: String = "",
    ) {
        composeRule.setContent {
            OpenpayTheme {
                OpenpayCardPreview(
                    holderName = holderName,
                    cardNumber = cardNumber,
                    expiration = expiration,
                )
            }
        }
    }

    @Test
    fun `empty preview shows fully masked number, holder placeholder and masked expiration`() {
        renderPreview()

        composeRule.onNodeWithText(
            "•••• •••• •••• ••••",
            useUnmergedTree = true,
        ).assertExists()
        composeRule.onNodeWithText("CARDHOLDER", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithText("••/••", useUnmergedTree = true).assertExists()
    }

    @Test
    fun `typed values are mirrored with the remaining positions masked`() {
        renderPreview(
            holderName = "Juan Pérez",
            cardNumber = "41111111",
            expiration = "123",
        )

        composeRule.onNodeWithText(
            "4111 1111 •••• ••••",
            useUnmergedTree = true,
        ).assertExists()
        composeRule.onNodeWithText("JUAN PÉREZ", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithText("12/3•", useUnmergedTree = true).assertExists()
    }

    @Test
    fun `a recognized brand shows its logo on the card`() {
        renderPreview(cardNumber = "4111")

        composeRule.onNodeWithTag(
            OpenpayFormTags.CARD_PREVIEW_BRAND_LOGO,
            useUnmergedTree = true,
        ).assertExists()
    }

    @Test
    fun `an unknown brand shows no logo on the card`() {
        renderPreview(cardNumber = "6011")

        composeRule.onNodeWithTag(
            OpenpayFormTags.CARD_PREVIEW_BRAND_LOGO,
            useUnmergedTree = true,
        ).assertDoesNotExist()
    }

    @Test
    fun `preview exposes a single content description without card digits`() {
        renderPreview(cardNumber = "4111111111111111")

        composeRule.onNodeWithTag(OpenpayFormTags.CARD_PREVIEW)
            .assertIsDisplayed()
            .assertContentDescriptionEquals("Preview of the card you are entering")
    }
}
