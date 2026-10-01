package mx.dev1.openpay.sdk.domain.repository

import mx.dev1.openpay.sdk.domain.model.Card
import mx.dev1.openpay.sdk.domain.model.Token

/**
 * Exchanges card data for Openpay tokens.
 */
interface TokenRepository {

    /**
     * Creates a token for [card].
     *
     * @throws mx.dev1.openpay.sdk.core.OpenpayException.ServiceError when the API rejects the card.
     * @throws mx.dev1.openpay.sdk.core.OpenpayException.ConnectionError when the API cannot be reached.
     */
    suspend fun createToken(card: Card): Token
}
