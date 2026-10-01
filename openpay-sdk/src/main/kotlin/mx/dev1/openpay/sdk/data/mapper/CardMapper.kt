package mx.dev1.openpay.sdk.data.mapper

import mx.dev1.openpay.sdk.data.remote.dto.CardDto
import mx.dev1.openpay.sdk.domain.model.Card
import mx.dev1.openpay.sdk.domain.model.TokenizedCard

internal fun Card.toRequestDto(): CardDto =
    CardDto(
        cardNumber = cardNumber,
        holderName = holderName,
        expirationMonth = formattedExpirationMonth,
        expirationYear = formattedExpirationYear,
        securityCode = securityCode,
        address = address?.toDto(),
    )

internal fun CardDto.toDomain(): TokenizedCard =
    TokenizedCard(
        maskedCardNumber = cardNumber,
        holderName = holderName,
        expirationMonth = expirationMonth,
        expirationYear = expirationYear,
        brand = brand,
        cardType = cardType,
        bankName = bankName,
        bankCode = bankCode,
        allowsCharges = allowsCharges,
        allowsPayouts = allowsPayouts,
        address = address?.toDomain(),
    )
