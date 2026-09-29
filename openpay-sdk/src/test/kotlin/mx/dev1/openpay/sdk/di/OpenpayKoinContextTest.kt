package mx.dev1.openpay.sdk.di

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import mx.dev1.openpay.sdk.core.OpenpayConfig
import mx.dev1.openpay.sdk.data.remote.OpenpayHttpClientFactory
import mx.dev1.openpay.sdk.data.remote.OpenpayResponseHandler
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenpayKoinContextTest {

    private val config = OpenpayConfig(merchantId = "merchant-id", publicApiKey = "pk_test_key")

    @After
    fun tearDown() {
        OpenpayKoinContext.stop()
    }

    @Test
    fun `start exposes every core definition`() {
        OpenpayKoinContext.start(config)

        assertTrue(OpenpayKoinContext.isStarted)
        assertEquals(config, OpenpayKoinContext.koin.get<OpenpayConfig>())
        assertNotNull(OpenpayKoinContext.koin.get<Json>())
        assertNotNull(OpenpayKoinContext.koin.get<OpenpayHttpClientFactory>())
        assertNotNull(OpenpayKoinContext.koin.get<HttpClient>())
        assertNotNull(OpenpayKoinContext.koin.get<OpenpayResponseHandler>())
    }

    @Test
    fun `starting again replaces the previous container`() {
        OpenpayKoinContext.start(config)
        val replacementConfig = OpenpayConfig(merchantId = "other-merchant", publicApiKey = "pk_other")

        OpenpayKoinContext.start(replacementConfig)

        assertEquals(replacementConfig, OpenpayKoinContext.koin.get<OpenpayConfig>())
    }

    @Test
    fun `using the container before starting fails with a clear message`() {
        assertFalse(OpenpayKoinContext.isStarted)

        val error = assertThrows(IllegalStateException::class.java) {
            OpenpayKoinContext.koin
        }
        assertTrue(checkNotNull(error.message).contains("has not been started"))
    }

    @Test
    fun `stop closes the container`() {
        OpenpayKoinContext.start(config)

        OpenpayKoinContext.stop()

        assertFalse(OpenpayKoinContext.isStarted)
    }
}
