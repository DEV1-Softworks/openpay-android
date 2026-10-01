package mx.dev1.openpay.sdk.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Error body returned by the Openpay API.
 * See https://documents.openpay.mx/docs/api/#errores
 */
@Serializable
internal data class OpenpayApiErrorDto(
    @SerialName("category") val category: String? = null,
    @SerialName("error_code") val errorCode: Int? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("http_code") val httpCode: Int? = null,
    @SerialName("request_id") val requestId: String? = null,
)
