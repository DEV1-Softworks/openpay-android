package mx.dev1.openpay.sdk.data.mapper

import mx.dev1.openpay.sdk.data.remote.dto.TokenDto
import mx.dev1.openpay.sdk.domain.model.Token

internal fun TokenDto.toDomain(): Token =
    Token(
        tokenId = tokenId,
        card = card?.toDomain(),
    )
