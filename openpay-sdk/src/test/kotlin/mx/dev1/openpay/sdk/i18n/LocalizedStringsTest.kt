package mx.dev1.openpay.sdk.i18n

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import mx.dev1.openpay.sdk.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Verifies the automatic locale resolution: the SDK strings follow the
 * device language for every bundled translation.
 */
@RunWith(RobolectricTestRunner::class)
class LocalizedStringsTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun `english is the default language`() {
        assertEquals("Card number", context.getString(R.string.openpay_card_number_label))
        assertEquals("Save card", context.getString(R.string.openpay_pay_button))
    }

    @Test
    @Config(qualifiers = "es")
    fun `spanish devices get spanish strings`() {
        assertEquals("Número de tarjeta", context.getString(R.string.openpay_card_number_label))
        assertEquals("Guardar tarjeta", context.getString(R.string.openpay_pay_button))
    }

    @Test
    @Config(qualifiers = "pt")
    fun `portuguese devices get portuguese strings`() {
        assertEquals("Número do cartão", context.getString(R.string.openpay_card_number_label))
        assertEquals("Salvar cartão", context.getString(R.string.openpay_pay_button))
    }

    @Test
    @Config(qualifiers = "fr")
    fun `french devices get french strings`() {
        assertEquals("Numéro de carte", context.getString(R.string.openpay_card_number_label))
        assertEquals("Enregistrer la carte", context.getString(R.string.openpay_pay_button))
    }

    @Test
    @Config(qualifiers = "de")
    fun `unbundled languages fall back to english`() {
        assertEquals("Card number", context.getString(R.string.openpay_card_number_label))
    }
}
