package mx.dev1.openpay.sdk.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import mx.dev1.openpay.sdk.core.OpenpayConfig
import mx.dev1.openpay.sdk.core.OpenpayException
import mx.dev1.openpay.sdk.data.mapper.toDomain
import mx.dev1.openpay.sdk.data.mapper.toRequestDto
import mx.dev1.openpay.sdk.data.remote.OpenpayResponseHandler
import mx.dev1.openpay.sdk.data.remote.dto.TokenDto
import mx.dev1.openpay.sdk.domain.model.Card
import mx.dev1.openpay.sdk.domain.model.Token
import mx.dev1.openpay.sdk.domain.repository.TokenRepository

/**
 * [TokenRepository] backed by the Openpay REST API:
 * POST {baseUrl}/v1/{merchantId}/tokens
 */
internal class RemoteTokenRepository(
    private val httpClient: HttpClient,
    private val responseHandler: OpenpayResponseHandler,
    private val config: OpenpayConfig,
) : TokenRepository {

    override suspend fun createToken(card: Card): Token {
        val response = executeTokenRequest(card)
        val tokenDto = responseHandler.parse<TokenDto>(response)
        return tokenDto.toDomain()
    }

    private suspend fun executeTokenRequest(card: Card): HttpResponse =
        try {
            httpClient.post("v1/${config.merchantId}/tokens") {
                setBody(card.toRequestDto())
            }
        } catch (interruption: kotlinx.coroutines.CancellationException) {
            throw interruption
        } catch (transportFailure: Exception) {
            throw OpenpayException.ConnectionError(
                message = "Could not reach the Openpay API",
                cause = transportFailure,
            )
        }
}
