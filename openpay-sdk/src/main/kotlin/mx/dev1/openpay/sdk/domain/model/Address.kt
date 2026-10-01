package mx.dev1.openpay.sdk.domain.model

/**
 * Billing address optionally attached to a card before tokenizing it.
 */
data class Address(
    val line1: String,
    val line2: String? = null,
    val line3: String? = null,
    val postalCode: String,
    val city: String,
    val state: String,
    val countryCode: String,
)
