package mx.dev1.openpay.sdk.domain.usecase

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.test.runTest
import mx.dev1.openpay.sdk.core.OpenpayException
import mx.dev1.openpay.sdk.domain.model.Card
import mx.dev1.openpay.sdk.domain.model.Token
import mx.dev1.openpay.sdk.domain.repository.TokenRepository
import mx.dev1.openpay.sdk.domain.validation.CardField
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CreateTokenUseCaseTest {

    private val frozenClock: Clock =
        Clock.fixed(Instant.parse("2026-06-15T12:00:00Z"), ZoneOffset.UTC)

    private val tokenRepository: TokenRepository = mock()
    private val createToken = CreateTokenUseCase(tokenRepository, frozenClock)

    private val validCard = Card(
        holderName = "Juan Pérez Ramírez",
        cardNumber = "4111111111111111",
        expirationMonth = 12,
        expirationYear = 30,
        securityCode = "110",
    )

    @Test
    fun `a valid card is sent to the repository`() = runTest {
        val expectedToken = Token(tokenId = "tok_123", card = null)
        whenever(tokenRepository.createToken(validCard)).thenReturn(expectedToken)

        val token = createToken(validCard)

        assertEquals(expectedToken, token)
        verify(tokenRepository).createToken(validCard)
    }

    @Test
    fun `an invalid card fails locally and never reaches the network`() = runTest {
        val expiredCard = validCard.copy(expirationMonth = 1, expirationYear = 20)

        try {
            createToken(expiredCard)
            throw AssertionError("A validation error was expected")
        } catch (validationError: OpenpayException.ValidationError) {
            assertEquals(
                setOf(CardField.EXPIRATION),
                validationError.validationResult.invalidFields,
            )
        }
        verify(tokenRepository, never()).createToken(any())
    }
}
