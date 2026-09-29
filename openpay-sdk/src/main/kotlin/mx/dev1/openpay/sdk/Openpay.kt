package mx.dev1.openpay.sdk

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.dev1.openpay.sdk.core.OpenpayConfig
import mx.dev1.openpay.sdk.core.OpenpayCountry
import mx.dev1.openpay.sdk.core.OpenpayEnvironment
import mx.dev1.openpay.sdk.core.OpenpayException
import mx.dev1.openpay.sdk.di.OpenpayKoinContext
import mx.dev1.openpay.sdk.di.openpayDataModule
import mx.dev1.openpay.sdk.domain.model.Card
import mx.dev1.openpay.sdk.domain.model.Token
import mx.dev1.openpay.sdk.domain.usecase.CreateTokenUseCase

/**
 * Entry point of the Openpay SDK.
 *
 * ```kotlin
 * val openpay = Openpay(
 *     OpenpayConfig(
 *         merchantId = "your-merchant-id",
 *         publicApiKey = "pk_your_public_key",
 *         country = OpenpayCountry.MEXICO,
 *         environment = OpenpayEnvironment.SANDBOX,
 *     )
 * )
 * val tokenResult = openpay.createToken(card)
 * ```
 *
 * Only one [Openpay] instance should be alive at a time; creating a new
 * instance rebuilds the SDK dependency graph with the new configuration.
 */
class Openpay internal constructor(
    private val createTokenUseCase: CreateTokenUseCase,
    backgroundDispatcher: CoroutineDispatcher,
    private val callbackDispatcher: CoroutineDispatcher,
) {

    /** Creates the SDK from a full [OpenpayConfig]. */
    constructor(config: OpenpayConfig) : this(
        createTokenUseCase = bootDependencies(config),
        backgroundDispatcher = Dispatchers.IO,
        callbackDispatcher = Dispatchers.Main.immediate,
    )

    /**
     * Legacy-style convenience constructor mirroring
     * `Openpay(merchantId, publicApiKey, productionMode, country)`.
     */
    constructor(
        merchantId: String,
        publicApiKey: String,
        productionMode: Boolean = false,
        country: OpenpayCountry = OpenpayCountry.MEXICO,
    ) : this(
        OpenpayConfig(
            merchantId = merchantId,
            publicApiKey = publicApiKey,
            country = country,
            environment = if (productionMode) OpenpayEnvironment.PRODUCTION else OpenpayEnvironment.SANDBOX,
        )
    )

    private val sdkScope = CoroutineScope(SupervisorJob() + backgroundDispatcher)

    /**
     * Validates [card] locally and exchanges it for a token.
     * The failure inside the returned [Result] is always an [OpenpayException].
     */
    suspend fun createToken(card: Card): Result<Token> =
        try {
            Result.success(createTokenUseCase(card))
        } catch (openpayError: OpenpayException) {
            Result.failure(openpayError)
        }

    /**
     * Callback flavor of [createToken] for consumers without coroutines.
     * The callback runs on the Android main thread.
     */
    fun createToken(card: Card, callback: OpenpayCallback<Token>) {
        sdkScope.launch {
            val tokenResult = createToken(card)
            withContext(callbackDispatcher) {
                tokenResult.fold(
                    onSuccess = callback::onSuccess,
                    onFailure = { failure -> callback.onError(failure as OpenpayException) },
                )
            }
        }
    }

    /**
     * Cancels in-flight operations and releases the SDK dependency graph.
     * Call it when the SDK is no longer needed (for example on logout).
     */
    fun shutdown() {
        sdkScope.cancel()
        OpenpayKoinContext.stop()
    }

    private companion object {

        fun bootDependencies(config: OpenpayConfig): CreateTokenUseCase {
            OpenpayKoinContext.start(
                config = config,
                extraModules = listOf(openpayDataModule()),
            )
            return OpenpayKoinContext.koin.get()
        }
    }
}
