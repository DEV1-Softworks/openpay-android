package mx.dev1.openpay.ui.checkout

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import mx.dev1.openpay.sdk.Openpay
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme
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

    private fun renderScreen() {
        composeRule.setContent {
            OpenpayTheme {
                CheckoutScreen(viewModel = viewModel)
            }
        }
    }

    @Test
    fun `typing the merchant configuration updates the state`() {
        renderScreen()

        composeRule.onNodeWithTag(CheckoutScreenTags.MERCHANT_ID_FIELD)
            .performTextInput("merchant-id")
        composeRule.onNodeWithTag(CheckoutScreenTags.PUBLIC_KEY_FIELD)
            .performTextInput("pk_test")

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
    fun `the sdk card form is embedded in the screen`() {
        renderScreen()

        composeRule.onNodeWithText("Card number").assertExists()
        composeRule.onNodeWithText("Merchant configuration").assertIsDisplayed()
    }
}
