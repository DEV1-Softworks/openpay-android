package mx.dev1.openpay.sdk.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class OpenpayConfigTest {

    @Test
    fun `defaults to mexico sandbox`() {
        val config = OpenpayConfig(merchantId = "merchant", publicApiKey = "public-key")

        assertEquals(OpenpayCountry.MEXICO, config.country)
        assertEquals(OpenpayEnvironment.SANDBOX, config.environment)
        assertEquals("https://sandbox-api.openpay.mx", config.baseUrl)
    }

    @Test
    fun `base url follows country and environment`() {
        val config = OpenpayConfig(
            merchantId = "merchant",
            publicApiKey = "public-key",
            country = OpenpayCountry.PERU,
            environment = OpenpayEnvironment.PRODUCTION,
        )

        assertEquals("https://api.openpay.pe", config.baseUrl)
    }

    @Test
    fun `blank merchant id is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            OpenpayConfig(merchantId = " ", publicApiKey = "public-key")
        }
    }

    @Test
    fun `blank public api key is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            OpenpayConfig(merchantId = "merchant", publicApiKey = "")
        }
    }
}
