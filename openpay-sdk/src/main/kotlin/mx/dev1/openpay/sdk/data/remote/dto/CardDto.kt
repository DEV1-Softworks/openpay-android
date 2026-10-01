package mx.dev1.openpay.sdk.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Card payload for token creation requests and card echo inside token responses.
 * Response-only fields stay null when serializing a request because of
 * kotlinx.serialization default-value omission.
 */
@Serializable
internal data class CardDto(
    @SerialName("card_number") val cardNumber: String? = null,
    @SerialName("holder_name") val holderName: String? = null,
    @SerialName("expiration_month") val expirationMonth: String? = null,
    @SerialName("expiration_year") val expirationYear: String? = null,
    @SerialName("cvv2") val securityCode: String? = null,
    @SerialName("address") val address: AddressDto? = null,
    @SerialName("brand") val brand: String? = null,
    @SerialName("type") val cardType: String? = null,
    @SerialName("bank_name") val bankName: String? = null,
    @SerialName("bank_code") val bankCode: String? = null,
    @SerialName("allows_charges") val allowsCharges: Boolean? = null,
    @SerialName("allows_payouts") val allowsPayouts: Boolean? = null,
)
