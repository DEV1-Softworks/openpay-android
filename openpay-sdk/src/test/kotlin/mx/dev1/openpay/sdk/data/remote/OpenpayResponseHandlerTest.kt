package mx.dev1.openpay.sdk.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mx.dev1.openpay.sdk.core.OpenpayException
import mx.dev1.openpay.sdk.data.remote.dto.TokenDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OpenpayResponseHandlerTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val responseHandler = OpenpayResponseHandler(json)

    private fun clientRespondingWith(body: String, status: HttpStatusCode): HttpClient =
        HttpClient(MockEngine { respond(body, status, headersOf(HttpHeaders.ContentType, "application/json")) }) {
            expectSuccess = false
            install(ContentNegotiation) { json(json) }
        }

    @Test
    fun `successful response returns the parsed body`() = runTest {
        val responseBody = """{"id":"tok_123","card":{"brand":"visa"}}"""

        clientRespondingWith(responseBody, HttpStatusCode.Created).use { httpClient ->
            val token = responseHandler.parse<TokenDto>(httpClient.get("tokens"))

            assertEquals("tok_123", token.tokenId)
            assertEquals("visa", token.card?.brand)
        }
    }

    @Test
    fun `api error body becomes a service error`() = runTest {
        val errorBody = """
            {"category":"request","error_code":2005,"description":"The expiration date has already passed",
             "http_code":400,"request_id":"req-1"}
        """.trimIndent()

        clientRespondingWith(errorBody, HttpStatusCode.BadRequest).use { httpClient ->
            try {
                responseHandler.parse<TokenDto>(httpClient.get("tokens"))
                throw AssertionError("A service error was expected")
            } catch (serviceError: OpenpayException.ServiceError) {
                assertEquals(2005, serviceError.errorCode)
                assertEquals("request", serviceError.category)
                assertEquals(400, serviceError.httpStatus)
                assertEquals("req-1", serviceError.requestId)
                assertEquals("The expiration date has already passed", serviceError.message)
            }
        }
    }

    @Test
    fun `unparseable error body still becomes a service error`() = runTest {
        clientRespondingWith("<html>gateway error</html>", HttpStatusCode.BadGateway).use { httpClient ->
            try {
                responseHandler.parse<TokenDto>(httpClient.get("tokens"))
                throw AssertionError("A service error was expected")
            } catch (serviceError: OpenpayException.ServiceError) {
                assertEquals(OpenpayResponseHandler.UNKNOWN_ERROR_CODE, serviceError.errorCode)
                assertEquals(502, serviceError.httpStatus)
                assertNull(serviceError.category)
            }
        }
    }
}
