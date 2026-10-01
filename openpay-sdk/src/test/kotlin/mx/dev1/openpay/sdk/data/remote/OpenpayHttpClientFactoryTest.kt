package mx.dev1.openpay.sdk.data.remote

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import java.util.Base64
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mx.dev1.openpay.sdk.OpenpaySdk
import mx.dev1.openpay.sdk.core.OpenpayConfig
import mx.dev1.openpay.sdk.core.OpenpayCountry
import mx.dev1.openpay.sdk.core.OpenpayEnvironment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenpayHttpClientFactoryTest {

    private val config = OpenpayConfig(
        merchantId = "merchant-id",
        publicApiKey = "pk_test_key",
        country = OpenpayCountry.MEXICO,
        environment = OpenpayEnvironment.SANDBOX,
    )

    private val factory = OpenpayHttpClientFactory(config = config, json = Json)

    @Test
    fun `requests carry basic auth with the public key and empty password`() = runTest {
        val mockEngine = MockEngine { respond("{}", HttpStatusCode.OK, jsonHeaders()) }

        factory.create(mockEngine).use { httpClient ->
            httpClient.get("v1/merchant-id/tokens")
        }

        val sentRequest = mockEngine.requestHistory.single()
        val expectedCredentials = Base64.getEncoder().encodeToString("pk_test_key:".toByteArray())
        assertEquals("Basic $expectedCredentials", sentRequest.headers[HttpHeaders.Authorization])
    }

    @Test
    fun `requests carry the sdk user agent`() = runTest {
        val mockEngine = MockEngine { respond("{}", HttpStatusCode.OK, jsonHeaders()) }

        factory.create(mockEngine).use { httpClient ->
            httpClient.get("v1/merchant-id/tokens")
        }

        val sentRequest = mockEngine.requestHistory.single()
        assertEquals(OpenpaySdk.userAgent, sentRequest.headers[HttpHeaders.UserAgent])
    }

    @Test
    fun `requests resolve against the configured base url`() = runTest {
        val mockEngine = MockEngine { respond("{}", HttpStatusCode.OK, jsonHeaders()) }

        factory.create(mockEngine).use { httpClient ->
            httpClient.get("v1/merchant-id/tokens")
        }

        val sentRequestUrl = mockEngine.requestHistory.single().url.toString()
        assertTrue(sentRequestUrl.startsWith("https://sandbox-api.openpay.mx"))
        assertTrue(sentRequestUrl.endsWith("v1/merchant-id/tokens"))
    }

    private fun jsonHeaders() = headersOf(HttpHeaders.ContentType, "application/json")
}
