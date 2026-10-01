package mx.dev1.openpay.ui.addcard

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import mx.dev1.openpay.sdk.domain.model.Card
import mx.dev1.openpay.sdk.domain.model.Token
import mx.dev1.openpay.sdk.domain.model.TokenizedCard
import mx.dev1.openpay.sdk.ui.components.OpenpayFormTags
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme
import mx.dev1.openpay.ui.checkout.CheckoutUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AddCardScreenComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var backRequested = false
    private var validatedCard: Card? = null

    private fun renderScreen(uiState: CheckoutUiState = CheckoutUiState()) {
        composeRule.setContent {
            OpenpayTheme {
                AddCardScreenContent(
                    uiState = uiState,
                    onCardValidated = { card -> validatedCard = card },
                    onNavigateBack = { backRequested = true },
                )
            }
        }
    }

    @Test
    fun `the sdk card form with its preview is embedded in the screen`() {
        renderScreen()

        composeRule.onNodeWithTag(OpenpayFormTags.CARD_PREVIEW).assertIsDisplayed()
        composeRule.onNodeWithText("Card number").assertExists()
    }

    @Test
    fun `the back button asks for navigation back`() {
        renderScreen()

        composeRule.onNodeWithTag(AddCardScreenTags.BACK_BUTTON).performClick()

        assertTrue(backRequested)
    }

    @Test
    fun `a created token is displayed with its masked card`() {
        renderScreen(
            uiState = CheckoutUiState(
                createdToken = Token(
                    tokenId = "tok-123",
                    card = TokenizedCard(
                        maskedCardNumber = "411111XXXXXX1111",
                        holderName = "Juan Pérez",
                        expirationMonth = "12",
                        expirationYear = "30",
                        brand = "visa",
                        cardType = "debit",
                        bankName = null,
                        bankCode = null,
                        allowsCharges = true,
                        allowsPayouts = false,
                        address = null,
                    ),
                ),
            ),
        )

        composeRule.onNodeWithTag(AddCardScreenTags.TOKEN_RESULT).assertExists()
        composeRule.onNodeWithText("visa 411111XXXXXX1111").assertExists()
        composeRule.onNodeWithTag(OpenpayFormTags.CARD_BRAND_LOGO, useUnmergedTree = true)
            .assertContentDescriptionEquals("Visa")
    }

    @Test
    fun `a token with an unrecognized brand shows no logo`() {
        renderScreen(
            uiState = CheckoutUiState(
                createdToken = Token(
                    tokenId = "tok-456",
                    card = TokenizedCard(
                        maskedCardNumber = "601100XXXXXX0004",
                        holderName = "Juan Pérez",
                        expirationMonth = "12",
                        expirationYear = "30",
                        brand = "carnet",
                        cardType = "debit",
                        bankName = null,
                        bankCode = null,
                        allowsCharges = true,
                        allowsPayouts = false,
                        address = null,
                    ),
                ),
            ),
        )

        composeRule.onNodeWithTag(OpenpayFormTags.CARD_BRAND_LOGO, useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun `a tokenization error is displayed`() {
        renderScreen(uiState = CheckoutUiState(errorMessage = "the card was declined"))

        composeRule.onNodeWithTag(AddCardScreenTags.ERROR_MESSAGE).assertExists()
        composeRule.onNodeWithText("the card was declined").assertExists()
    }
}
