package mx.dev1.openpay.sdk.core

/**
 * Base type for every error surfaced by the Openpay SDK.
 */
sealed class OpenpayException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    /**
     * The Openpay API rejected the request. Mirrors the error body documented at
     * https://documents.openpay.mx/docs/api/#errores
     *
     * @property errorCode Openpay-specific error code (for example 1001 for a malformed request).
     * @property category Error category reported by the API: request, internal or gateway.
     * @property httpStatus HTTP status code of the rejected response.
     * @property requestId Identifier of the request inside Openpay, useful for support tickets.
     */
    class ServiceError(
        val errorCode: Int,
        val category: String?,
        val httpStatus: Int,
        val requestId: String?,
        description: String,
    ) : OpenpayException(description)

    /**
     * The Openpay API could not be reached (no connectivity, DNS failure, timeout)
     * or returned a response the SDK could not understand.
     */
    class ConnectionError(
        message: String,
        cause: Throwable? = null,
    ) : OpenpayException(message, cause)
}
