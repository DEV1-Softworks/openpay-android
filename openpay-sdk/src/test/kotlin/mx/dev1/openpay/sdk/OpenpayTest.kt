package mx.dev1.openpay.sdk

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import mx.dev1.openpay.sdk.core.OpenpayException
import mx.dev1.openpay.sdk.domain.model.Card
import mx.dev1.openpay.sdk.domain.model.Token
import mx.dev1.openpay.sdk.domain.repository.TokenRepository
import mx.dev1.openpay.sdk.domain.usecase.CreateTokenUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class OpenpayTest {

    private val frozenClock: Clock =
        Clock.fixed(Instant.parse("2026-06-15T12:00:00Z"), ZoneOffset.UTC)

    private val tokenRepository: TokenRepository = mock()

    private val validCard = Card(
        holderName = "Juan Pérez Ramírez",
        cardNumber = "4111111111111111",
        expirationMonth = 12,
        expirationYear = 30,
        securityCode = "110",
    )

    private fun openpayWith(dispatcher: kotlinx.coroutines.CoroutineDispatcher) = Openpay(
        createTokenUseCase = CreateTokenUseCase(tokenRepository, frozenClock),
        backgroundDispatcher = dispatcher,
        callbackDispatcher = dispatcher,
    )

    @Test
    fun `suspend createToken returns a successful result`() = runTest {
        val expectedToken = Token(tokenId = "tok_123", card = null)
        whenever(tokenRepository.createToken(validCard)).thenReturn(expectedToken)
        val openpay = openpayWith(StandardTestDispatcher(testScheduler))

        val tokenResult = openpay.createToken(validCard)

        assertEquals(expectedToken, tokenResult.getOrNull())
    }

    @Test
    fun `suspend createToken wraps openpay errors in a failed result`() = runTest {
        val invalidCard = validCard.copy(cardNumber = "1234")
        val openpay = openpayWith(StandardTestDispatcher(testScheduler))

        val tokenResult = openpay.createToken(invalidCard)

        assertTrue(tokenResult.exceptionOrNull() is OpenpayException.ValidationError)
    }

    @Test
    fun `callback createToken reports success on the callback dispatcher`() = runTest {
        val expectedToken = Token(tokenId = "tok_456", card = null)
        whenever(tokenRepository.createToken(validCard)).thenReturn(expectedToken)
        val openpay = openpayWith(StandardTestDispatcher(testScheduler))

        var receivedToken: Token? = null
        var receivedError: OpenpayException? = null
        openpay.createToken(
            validCard,
            object : OpenpayCallback<Token> {
                override fun onSuccess(result: Token) {
                    receivedToken = result
                }

                override fun onError(exception: OpenpayException) {
                    receivedError = exception
                }
            },
        )
        advanceUntilIdle()

        assertEquals(expectedToken, receivedToken)
        assertNull(receivedError)
    }

    @Test
    fun `callback createToken reports validation errors`() = runTest {
        val invalidCard = validCard.copy(securityCode = "1")
        val openpay = openpayWith(StandardTestDispatcher(testScheduler))

        var receivedError: OpenpayException? = null
        openpay.createToken(
            invalidCard,
            object : OpenpayCallback<Token> {
                override fun onSuccess(result: Token) {
                    throw AssertionError("An error was expected")
                }

                override fun onError(exception: OpenpayException) {
                    receivedError = exception
                }
            },
        )
        advanceUntilIdle()

        assertTrue(receivedError is OpenpayException.ValidationError)
    }
}
