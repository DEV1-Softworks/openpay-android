package mx.dev1.openpay.sdk.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import mx.dev1.openpay.sdk.domain.model.Card
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OpenpayCardFormComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var validatedCard: Card? = null

    private fun renderForm() {
        composeRule.setContent {
            OpenpayTheme {
                OpenpayCardForm(onCardValidated = { card -> validatedCard = card })
            }
        }
    }

    private fun fillValidCard() {
        composeRule.onNodeWithTag(OpenpayFormTags.HOLDER_NAME_FIELD)
            .performTextInput("Juan Pérez Ramírez")
        composeRule.onNodeWithTag(OpenpayFormTags.CARD_NUMBER_FIELD)
            .performTextInput("4111111111111111")
        composeRule.onNodeWithTag(OpenpayFormTags.EXPIRATION_FIELD)
            .performTextInput("1230")
        composeRule.onNodeWithTag(OpenpayFormTags.SECURITY_CODE_FIELD)
            .performTextInput("110")
    }

    @Test
    fun `submitting a valid card delivers it to the callback`() {
        renderForm()
        fillValidCard()

        composeRule.onNodeWithTag(OpenpayFormTags.SUBMIT_BUTTON).performClick()

        val card = checkNotNull(validatedCard)
        assertEquals("4111111111111111", card.cardNumber)
        assertEquals(12, card.expirationMonth)
        assertEquals(30, card.expirationYear)
        assertEquals("110", card.securityCode)
    }

    @Test
    fun `submitting an empty form shows errors and keeps the callback silent`() {
        renderForm()

        composeRule.onNodeWithTag(OpenpayFormTags.SUBMIT_BUTTON).performClick()

        assertNull(validatedCard)
        composeRule.onNodeWithText("Enter a valid card number").assertIsDisplayed()
        composeRule.onNodeWithText("Enter the name as printed on the card").assertIsDisplayed()
    }

    @Test
    fun `typing a visa number shows the brand badge`() {
        renderForm()

        composeRule.onNodeWithTag(OpenpayFormTags.CARD_NUMBER_FIELD)
            .performTextInput("4111")

        composeRule.onNodeWithTag(OpenpayFormTags.BRAND_BADGE, useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun `card number shows formatted groups while typing`() {
        renderForm()

        composeRule.onNodeWithTag(OpenpayFormTags.CARD_NUMBER_FIELD)
            .performTextInput("41111111")

        composeRule.onNodeWithText("4111 1111").assertIsDisplayed()
    }

    @Test
    fun `card preview is shown by default and mirrors the typed number`() {
        renderForm()

        composeRule.onNodeWithTag(OpenpayFormTags.CARD_PREVIEW).assertIsDisplayed()

        composeRule.onNodeWithTag(OpenpayFormTags.CARD_NUMBER_FIELD)
            .performTextInput("41111111")

        composeRule.onNodeWithText("4111 1111 •••• ••••", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun `card preview can be disabled by the host app`() {
        composeRule.setContent {
            OpenpayTheme {
                OpenpayCardForm(
                    onCardValidated = { card -> validatedCard = card },
                    showCardPreview = false,
                )
            }
        }

        composeRule.onNodeWithTag(OpenpayFormTags.CARD_PREVIEW).assertDoesNotExist()
    }

    @Test
    fun `fixing a field after a failed submit clears its error`() {
        renderForm()
        composeRule.onNodeWithTag(OpenpayFormTags.SUBMIT_BUTTON).performClick()
        composeRule.onNodeWithText("Enter a valid card number").assertIsDisplayed()

        composeRule.onNodeWithTag(OpenpayFormTags.CARD_NUMBER_FIELD)
            .performTextInput("4111111111111111")

        composeRule.onNodeWithText("Enter a valid card number").assertDoesNotExist()
        assertNotNull(composeRule)
    }
}
