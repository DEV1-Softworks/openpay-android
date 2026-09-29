package mx.dev1.openpay.sdk.i18n

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OpenpayLanguageTest {

    @Test
    fun `plain language tags resolve`() {
        assertEquals(OpenpayLanguage.ENGLISH, OpenpayLanguage.fromLanguageTag("en"))
        assertEquals(OpenpayLanguage.SPANISH, OpenpayLanguage.fromLanguageTag("es"))
        assertEquals(OpenpayLanguage.PORTUGUESE, OpenpayLanguage.fromLanguageTag("pt"))
        assertEquals(OpenpayLanguage.FRENCH, OpenpayLanguage.fromLanguageTag("fr"))
    }

    @Test
    fun `regional tags resolve to their base language`() {
        assertEquals(OpenpayLanguage.SPANISH, OpenpayLanguage.fromLanguageTag("es-MX"))
        assertEquals(OpenpayLanguage.PORTUGUESE, OpenpayLanguage.fromLanguageTag("pt-BR"))
        assertEquals(OpenpayLanguage.FRENCH, OpenpayLanguage.fromLanguageTag("fr-CA"))
    }

    @Test
    fun `casing is ignored`() {
        assertEquals(OpenpayLanguage.SPANISH, OpenpayLanguage.fromLanguageTag("ES"))
    }

    @Test
    fun `unbundled languages resolve to null`() {
        assertNull(OpenpayLanguage.fromLanguageTag("de"))
        assertNull(OpenpayLanguage.fromLanguageTag(""))
    }
}
