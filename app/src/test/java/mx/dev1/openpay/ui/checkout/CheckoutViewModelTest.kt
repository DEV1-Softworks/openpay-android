package mx.dev1.openpay.ui.checkout

import android.app.Activity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mx.dev1.openpay.sdk.Openpay
import mx.dev1.openpay.sdk.core.OpenpayConfig
import mx.dev1.openpay.sdk.core.OpenpayCountry
import mx.dev1.openpay.sdk.core.OpenpayEnvironment
import mx.dev1.openpay.sdk.core.OpenpayException
import mx.dev1.openpay.sdk.domain.model.Card
import mx.dev1.openpay.sdk.domain.model.Token
import mx.dev1.openpay.sdk.i18n.OpenpayLanguage
import mx.dev1.openpay.sdk.i18n.OpenpayLocale
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CheckoutViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val openpay: Openpay = mock()
    private var builtConfigs = mutableListOf<OpenpayConfig>()

    private val viewModel = CheckoutViewModel(
        openpayFactory = { config ->
            builtConfigs.add(config)
            openpay
        },
    )

    private val validCard = Card(
        holderName = "Juan Pérez Ramírez",
        cardNumber = "4111111111111111",
        expirationMonth = 12,
        expirationYear = 30,
        securityCode = "110",
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        OpenpayLocale.override = null
    }

    private fun configureMerchant() {
        viewModel.updateMerchantId("merchant-id")
        viewModel.updatePublicApiKey("pk_test")
    }

    @Test
    fun `tokenizing without configuration reports an error and builds nothing`() = runTest(testDispatcher.scheduler) {
        viewModel.tokenize(validCard)

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(builtConfigs.isEmpty())
        verify(openpay, never()).createToken(any())
    }

    @Test
    fun `successful tokenization publishes the token`() = runTest(testDispatcher.scheduler) {
        configureMerchant()
        val expectedToken = Token(tokenId = "tok_123", card = null)
        whenever(openpay.createToken(validCard)).thenReturn(Result.success(expectedToken))

        viewModel.tokenize(validCard)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(expectedToken, state.createdToken)
        assertNull(state.errorMessage)
        assertEquals(false, state.isProcessing)
    }

    @Test
    fun `service rejections surface as readable errors`() = runTest(testDispatcher.scheduler) {
        configureMerchant()
        val rejection = OpenpayException.ServiceError(
            errorCode = 3001,
            category = "request",
            httpStatus = 402,
            requestId = null,
            description = "The card was declined",
        )
        whenever(openpay.createToken(validCard)).thenReturn(Result.failure(rejection))

        viewModel.tokenize(validCard)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.createdToken)
        assertTrue(checkNotNull(state.errorMessage).contains("3001"))
    }

    @Test
    fun `device session id is published on state`() = runTest(testDispatcher.scheduler) {
        configureMerchant()
        val activity: Activity = mock()
        whenever(openpay.setupDeviceSession(activity)).thenReturn("device-session-id")

        viewModel.startDeviceSession(activity)

        assertEquals("device-session-id", viewModel.uiState.value.deviceSessionId)
    }

    @Test
    fun `the sdk instance is reused while the configuration stays the same`() = runTest(testDispatcher.scheduler) {
        configureMerchant()
        whenever(openpay.createToken(any())).thenReturn(Result.success(Token("tok", null)))

        viewModel.tokenize(validCard)
        advanceUntilIdle()
        viewModel.tokenize(validCard)
        advanceUntilIdle()

        assertEquals(1, builtConfigs.size)
    }

    @Test
    fun `changing the configuration rebuilds the sdk`() = runTest(testDispatcher.scheduler) {
        configureMerchant()
        whenever(openpay.createToken(any())).thenReturn(Result.success(Token("tok", null)))

        viewModel.tokenize(validCard)
        advanceUntilIdle()
        viewModel.updateCountry(OpenpayCountry.PERU)
        viewModel.updateProductionMode(true)
        viewModel.tokenize(validCard)
        advanceUntilIdle()

        assertEquals(2, builtConfigs.size)
        verify(openpay).shutdown()
        assertEquals(OpenpayCountry.PERU, builtConfigs.last().country)
        assertEquals(OpenpayEnvironment.PRODUCTION, builtConfigs.last().environment)
    }

    @Test
    fun `language selection drives the sdk locale override`() {
        viewModel.updateLanguage(OpenpayLanguage.FRENCH)

        assertEquals(OpenpayLanguage.FRENCH, OpenpayLocale.override)
        assertEquals(OpenpayLanguage.FRENCH, viewModel.uiState.value.selectedLanguage)

        viewModel.updateLanguage(null)

        assertNull(OpenpayLocale.override)
    }
}
