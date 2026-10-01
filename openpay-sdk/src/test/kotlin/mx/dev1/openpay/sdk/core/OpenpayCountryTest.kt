package mx.dev1.openpay.sdk.core

import org.junit.Assert.assertEquals
import org.junit.Test

class OpenpayCountryTest {

    @Test
    fun `mexico resolves sandbox and production urls`() {
        assertEquals(
            "https://sandbox-api.openpay.mx",
            OpenpayCountry.MEXICO.baseUrlFor(OpenpayEnvironment.SANDBOX),
        )
        assertEquals(
            "https://api.openpay.mx",
            OpenpayCountry.MEXICO.baseUrlFor(OpenpayEnvironment.PRODUCTION),
        )
    }

    @Test
    fun `colombia resolves sandbox and production urls`() {
        assertEquals(
            "https://sandbox-api.openpay.co",
            OpenpayCountry.COLOMBIA.baseUrlFor(OpenpayEnvironment.SANDBOX),
        )
        assertEquals(
            "https://api.openpay.co",
            OpenpayCountry.COLOMBIA.baseUrlFor(OpenpayEnvironment.PRODUCTION),
        )
    }

    @Test
    fun `peru resolves sandbox and production urls`() {
        assertEquals(
            "https://sandbox-api.openpay.pe",
            OpenpayCountry.PERU.baseUrlFor(OpenpayEnvironment.SANDBOX),
        )
        assertEquals(
            "https://api.openpay.pe",
            OpenpayCountry.PERU.baseUrlFor(OpenpayEnvironment.PRODUCTION),
        )
    }
}
