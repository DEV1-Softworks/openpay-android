package mx.dev1.openpay.sdk.data.mapper

import mx.dev1.openpay.sdk.data.remote.dto.AddressDto
import mx.dev1.openpay.sdk.domain.model.Address

internal fun Address.toDto(): AddressDto =
    AddressDto(
        line1 = line1,
        line2 = line2,
        line3 = line3,
        postalCode = postalCode,
        city = city,
        state = state,
        countryCode = countryCode,
    )

internal fun AddressDto.toDomain(): Address? {
    val requiredFields = listOf(line1, postalCode, city, state, countryCode)
    if (requiredFields.any { it == null }) {
        return null
    }
    return Address(
        line1 = checkNotNull(line1),
        line2 = line2,
        line3 = line3,
        postalCode = checkNotNull(postalCode),
        city = checkNotNull(city),
        state = checkNotNull(state),
        countryCode = checkNotNull(countryCode),
    )
}
