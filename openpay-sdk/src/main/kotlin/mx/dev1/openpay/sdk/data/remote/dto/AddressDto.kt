package mx.dev1.openpay.sdk.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AddressDto(
    @SerialName("line1") val line1: String? = null,
    @SerialName("line2") val line2: String? = null,
    @SerialName("line3") val line3: String? = null,
    @SerialName("postal_code") val postalCode: String? = null,
    @SerialName("city") val city: String? = null,
    @SerialName("state") val state: String? = null,
    @SerialName("country_code") val countryCode: String? = null,
)
