package mx.dev1.openpay.sdk.domain.usecase

import java.time.Clock
import mx.dev1.openpay.sdk.core.OpenpayException
import mx.dev1.openpay.sdk.domain.model.Card
import mx.dev1.openpay.sdk.domain.model.Token
import mx.dev1.openpay.sdk.domain.repository.TokenRepository
import mx.dev1.openpay.sdk.domain.validation.CardValidator

/**
 * Validates the card locally and, when valid, exchanges it for a token.
 * Local validation avoids a network round trip for data the API would reject.
 */
class CreateTokenUseCase internal constructor(
    private val tokenRepository: TokenRepository,
    private val clock: Clock = Clock.systemDefaultZone(),
) {

    /**
     * @throws OpenpayException.ValidationError when the card data is invalid.
     * @throws OpenpayException.ServiceError when the API rejects the request.
     * @throws OpenpayException.ConnectionError when the API cannot be reached.
     */
    suspend operator fun invoke(card: Card): Token {
        val validationResult = CardValidator.validate(card, clock)
        if (!validationResult.isValid) {
            throw OpenpayException.ValidationError(validationResult)
        }
        return tokenRepository.createToken(card)
    }
}
