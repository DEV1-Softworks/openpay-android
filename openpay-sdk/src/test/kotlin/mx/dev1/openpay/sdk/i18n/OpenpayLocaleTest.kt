package mx.dev1.openpay.sdk.i18n

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import mx.dev1.openpay.sdk.R
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OpenpayLocaleTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @After
    fun tearDown() {
        OpenpayLocale.override = null
    }

    @Test
    fun `without override the context is returned untouched`() {
        OpenpayLocale.override = null

        assertSame(context, OpenpayLocale.localize(context))
    }

    @Test
    fun `override forces the strings of the chosen language`() {
        OpenpayLocale.override = OpenpayLanguage.FRENCH

        val localizedContext = OpenpayLocale.localize(context)

        assertEquals(
            "Numéro de carte",
            localizedContext.getString(R.string.openpay_card_number_label),
        )
    }

    @Test
    fun `override wins over the device language`() {
        OpenpayLocale.override = OpenpayLanguage.PORTUGUESE

        val localizedContext = OpenpayLocale.localize(context)

        assertEquals(
            "Número do cartão",
            localizedContext.getString(R.string.openpay_card_number_label),
        )
    }
}
