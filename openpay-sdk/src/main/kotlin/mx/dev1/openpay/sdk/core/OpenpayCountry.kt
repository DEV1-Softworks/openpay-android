package mx.dev1.openpay.sdk.core

/**
 * Countries where Openpay operates, each with its own API host.
 */
enum class OpenpayCountry(
    private val sandboxBaseUrl: String,
    private val productionBaseUrl: String,
) {
    MEXICO("https://sandbox-api.openpay.mx", "https://api.openpay.mx"),
    COLOMBIA("https://sandbox-api.openpay.co", "https://api.openpay.co"),
    PERU("https://sandbox-api.openpay.pe", "https://api.openpay.pe");

    /** Resolves the API base URL for this country in the given [environment]. */
    fun baseUrlFor(environment: OpenpayEnvironment): String =
        when (environment) {
            OpenpayEnvironment.SANDBOX -> sandboxBaseUrl
            OpenpayEnvironment.PRODUCTION -> productionBaseUrl
        }
}
