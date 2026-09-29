package mx.dev1.openpay.sdk.i18n

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import mx.dev1.openpay.sdk.ui.components.OpenpayCardForm
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OpenpayLocalizedComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @After
    fun tearDown() {
        OpenpayLocale.override = null
    }

    @Test
    fun `the form follows the language override`() {
        OpenpayLocale.override = OpenpayLanguage.SPANISH

        composeRule.setContent {
            OpenpayTheme {
                OpenpayCardForm(onCardValidated = {})
            }
        }

        composeRule.onNodeWithText("Número de tarjeta").assertIsDisplayed()
        composeRule.onNodeWithText("Guardar tarjeta").assertIsDisplayed()
    }

    @Test
    fun `changing the override recomposes the form`() {
        composeRule.setContent {
            OpenpayTheme {
                OpenpayCardForm(onCardValidated = {})
            }
        }
        composeRule.onNodeWithText("Card number").assertIsDisplayed()

        OpenpayLocale.override = OpenpayLanguage.FRENCH
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Numéro de carte").assertIsDisplayed()
    }
}
