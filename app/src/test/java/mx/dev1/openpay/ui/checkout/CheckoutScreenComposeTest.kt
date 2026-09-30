package mx.dev1.openpay.ui.checkout

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import mx.dev1.openpay.sdk.Openpay
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CheckoutScreenComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val openpay: Openpay = mock()
    private val viewModel = CheckoutViewModel(openpayFactory = { openpay })
    private var addCardRequested = false

    private fun renderScreen() {
        composeRule.setContent {
            OpenpayTheme {
                CheckoutScreen(
                    onAddCardClick = { addCardRequested = true },
                    viewModel = viewModel,
                )
            }
        }
    }

    private fun fillConfiguration() {
        composeRule.onNodeWithTag(CheckoutScreenTags.MERCHANT_ID_FIELD)
            .performTextInput("merchant-id")
        composeRule.onNodeWithTag(CheckoutScreenTags.PUBLIC_KEY_FIELD)
            .performTextInput("pk_test")
    }

    @Test
    fun `typing the merchant configuration updates the state`() {
        renderScreen()

        fillConfiguration()

        assert(viewModel.uiState.value.merchantId == "merchant-id")
        assert(viewModel.uiState.value.publicApiKey == "pk_test")
    }

    @Test
    fun `requesting a device session without configuration shows the error`() {
        renderScreen()

        composeRule.onNodeWithTag(CheckoutScreenTags.DEVICE_SESSION_BUTTON)
            .performScrollTo()
            .performClick()

        composeRule.onNodeWithTag(CheckoutScreenTags.ERROR_MESSAGE).assertExists()
    }

    @Test
    fun `the card form is not embedded in the configuration screen`() {
        renderScreen()

        composeRule.onNodeWithText("Merchant configuration").assertIsDisplayed()
        composeRule.onNodeWithText("Card number").assertDoesNotExist()
    }

    @Test
    fun `add card stays disabled until the configuration is complete`() {
        renderScreen()

        composeRule.onNodeWithTag(CheckoutScreenTags.ADD_CARD_BUTTON)
            .performScrollTo()
            .assertIsNotEnabled()

        fillConfiguration()

        composeRule.onNodeWithTag(CheckoutScreenTags.ADD_CARD_BUTTON).assertIsEnabled()
    }

    @Test
    fun `tapping add card asks for the card form screen`() {
        renderScreen()
        fillConfiguration()

        composeRule.onNodeWithTag(CheckoutScreenTags.ADD_CARD_BUTTON)
            .performScrollTo()
            .performClick()

        assertTrue(addCardRequested)
    }
}
