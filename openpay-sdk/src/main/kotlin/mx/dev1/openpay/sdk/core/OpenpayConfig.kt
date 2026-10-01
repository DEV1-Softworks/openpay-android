package mx.dev1.openpay.sdk.core

/**
 * Immutable configuration required to talk to the Openpay API.
 *
 * @property merchantId Merchant identifier assigned by Openpay when the account was created.
 * @property publicApiKey Public API key of the merchant. Never use the private key inside an app.
 * @property country Country of the merchant account, defines the API host.
 * @property environment Sandbox or production environment.
 */
data class OpenpayConfig(
    val merchantId: String,
    val publicApiKey: String,
    val country: OpenpayCountry = OpenpayCountry.MEXICO,
    val environment: OpenpayEnvironment = OpenpayEnvironment.SANDBOX,
) {

    init {
        require(merchantId.isNotBlank()) { "merchantId must not be blank" }
        require(publicApiKey.isNotBlank()) { "publicApiKey must not be blank" }
    }

    /** API base URL resolved from [country] and [environment]. */
    val baseUrl: String = country.baseUrlFor(environment)
}
