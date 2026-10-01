package mx.dev1.openpay.sdk.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TokenDto(
    @SerialName("id") val tokenId: String,
    @SerialName("card") val card: CardDto? = null,
)
