package mx.dev1.openpay.sdk.data.remote

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import mx.dev1.openpay.sdk.core.OpenpayException
import mx.dev1.openpay.sdk.data.remote.dto.OpenpayApiErrorDto

/**
 * Converts HTTP responses into API payloads or [OpenpayException]s.
 */
internal class OpenpayResponseHandler(private val json: Json) {

    /**
     * Returns the deserialized body when the response is successful, otherwise
     * throws [OpenpayException.ServiceError] built from the Openpay error body.
     */
    suspend inline fun <reified BodyType> parse(response: HttpResponse): BodyType {
        if (response.status.value in SUCCESSFUL_STATUS_RANGE) {
            return response.body()
        }
        throw serviceErrorFrom(response)
    }

    suspend fun serviceErrorFrom(response: HttpResponse): OpenpayException.ServiceError {
        val rawBody = runCatching { response.bodyAsText() }.getOrDefault("")
        val errorBody = runCatching {
            json.decodeFromString(OpenpayApiErrorDto.serializer(), rawBody)
        }.getOrNull()

        return OpenpayException.ServiceError(
            errorCode = errorBody?.errorCode ?: UNKNOWN_ERROR_CODE,
            category = errorBody?.category,
            httpStatus = response.status.value,
            requestId = errorBody?.requestId,
            description = errorBody?.description
                ?: "Openpay request failed with HTTP ${response.status.value}",
        )
    }

    companion object {
        val SUCCESSFUL_STATUS_RANGE = 200..299
        const val UNKNOWN_ERROR_CODE = -1
    }
}
