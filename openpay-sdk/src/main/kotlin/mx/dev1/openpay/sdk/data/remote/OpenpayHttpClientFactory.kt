package mx.dev1.openpay.sdk.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import java.util.Base64
import kotlinx.serialization.json.Json
import mx.dev1.openpay.sdk.OpenpaySdk
import mx.dev1.openpay.sdk.core.OpenpayConfig

/**
 * Builds the Ktor [HttpClient] used for every Openpay API call.
 * Requests authenticate with HTTP Basic using the merchant public key as
 * username and an empty password, exactly like the legacy SDK.
 */
internal class OpenpayHttpClientFactory(
    private val config: OpenpayConfig,
    private val json: Json,
) {

    fun create(engine: HttpClientEngine = CIO.create()): HttpClient =
        HttpClient(engine) {
            expectSuccess = false

            install(ContentNegotiation) {
                json(json)
            }

            install(HttpTimeout) {
                connectTimeoutMillis = CONNECTION_TIMEOUT_MILLIS
                requestTimeoutMillis = CONNECTION_TIMEOUT_MILLIS
                socketTimeoutMillis = CONNECTION_TIMEOUT_MILLIS
            }

            defaultRequest {
                url(config.baseUrl)
                contentType(ContentType.Application.Json)
                headers.append(HttpHeaders.Authorization, basicAuthorizationHeader(config.publicApiKey))
                headers.append(HttpHeaders.UserAgent, OpenpaySdk.userAgent)
            }
        }

    private fun basicAuthorizationHeader(publicApiKey: String): String {
        val credentials = "$publicApiKey:"
        val encodedCredentials = Base64.getEncoder().encodeToString(credentials.toByteArray(Charsets.UTF_8))
        return "Basic $encodedCredentials"
    }

    private companion object {
        const val CONNECTION_TIMEOUT_MILLIS = 60_000L
    }
}
