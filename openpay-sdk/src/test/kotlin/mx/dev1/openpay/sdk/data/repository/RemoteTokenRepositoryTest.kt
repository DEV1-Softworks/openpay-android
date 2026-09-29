package mx.dev1.openpay.sdk.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import java.io.IOException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mx.dev1.openpay.sdk.core.OpenpayConfig
import mx.dev1.openpay.sdk.core.OpenpayException
import mx.dev1.openpay.sdk.data.remote.OpenpayResponseHandler
import mx.dev1.openpay.sdk.domain.model.Card
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteTokenRepositoryTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val config = OpenpayConfig(merchantId = "merchant-id", publicApiKey = "pk_test")

    private val cardToTokenize = Card(
        holderName = "Juan Pérez Ramírez",
        cardNumber = "4111111111111111",
        expirationMonth = 12,
        expirationYear = 30,
        securityCode = "110",
    )

    private var lastRequest: HttpRequestData? = null

    private fun repositoryWith(handler: MockEngine): RemoteTokenRepository {
        val httpClient = HttpClient(handler) {
            expectSuccess = false
            install(ContentNegotiation) { json(json) }
            defaultRequest { contentType(ContentType.Application.Json) }
        }
        return RemoteTokenRepository(
            httpClient = httpClient,
            responseHandler = OpenpayResponseHandler(json),
            config = config,
        )
    }

    @Test
    fun `posts the card to the merchant tokens endpoint`() = runTest {
        val mockEngine = MockEngine { request ->
            lastRequest = request
            respond(
                """{"id":"tok_123","card":{"card_number":"411111XXXXXX1111","brand":"visa"}}""",
                HttpStatusCode.Created,
                headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        val token = repositoryWith(mockEngine).createToken(cardToTokenize)

        assertEquals("tok_123", token.tokenId)
        assertEquals("visa", token.card?.brand)
        val sentRequest = checkNotNull(lastRequest)
        assertEquals(HttpMethod.Post, sentRequest.method)
        assertTrue(sentRequest.url.toString().endsWith("v1/merchant-id/tokens"))
        val sentBody = (sentRequest.body as TextContent).text
        assertTrue(sentBody.contains(""""card_number":"4111111111111111""""))
        assertTrue(sentBody.contains(""""expiration_month":"12""""))
        assertTrue(sentBody.contains(""""expiration_year":"30""""))
        assertTrue(sentBody.contains(""""cvv2":"110""""))
    }

    @Test
    fun `api rejection surfaces as service error`() = runTest {
        val mockEngine = MockEngine {
            respond(
                """{"category":"request","error_code":3001,"description":"The card was declined","http_code":402}""",
                HttpStatusCode.PaymentRequired,
                headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        try {
            repositoryWith(mockEngine).createToken(cardToTokenize)
            throw AssertionError("A service error was expected")
        } catch (serviceError: OpenpayException.ServiceError) {
            assertEquals(3001, serviceError.errorCode)
            assertEquals(402, serviceError.httpStatus)
            assertEquals("The card was declined", serviceError.message)
        }
    }

    @Test
    fun `transport failures surface as connection error`() = runTest {
        val mockEngine = MockEngine {
            throw IOException("Network unreachable")
        }

        try {
            repositoryWith(mockEngine).createToken(cardToTokenize)
            throw AssertionError("A connection error was expected")
        } catch (connectionError: OpenpayException.ConnectionError) {
            val causeMessages = generateSequence(connectionError.cause) { failure -> failure.cause }
                .mapNotNull(Throwable::message)
                .toList()
            assertTrue(causeMessages.any { message -> message.contains("Network unreachable") })
        }
    }
}
