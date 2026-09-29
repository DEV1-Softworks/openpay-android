package mx.dev1.openpay.ui.checkout

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.dev1.openpay.sdk.Openpay
import mx.dev1.openpay.sdk.core.OpenpayConfig
import mx.dev1.openpay.sdk.core.OpenpayCountry
import mx.dev1.openpay.sdk.core.OpenpayEnvironment
import mx.dev1.openpay.sdk.core.OpenpayException
import mx.dev1.openpay.sdk.domain.model.Card
import mx.dev1.openpay.sdk.domain.model.Token
import mx.dev1.openpay.sdk.i18n.OpenpayLanguage
import mx.dev1.openpay.sdk.i18n.OpenpayLocale

data class CheckoutUiState(
    val merchantId: String = "",
    val publicApiKey: String = "",
    val country: OpenpayCountry = OpenpayCountry.MEXICO,
    val isProductionMode: Boolean = false,
    val selectedLanguage: OpenpayLanguage? = null,
    val deviceSessionId: String? = null,
    val createdToken: Token? = null,
    val errorMessage: String? = null,
    val isProcessing: Boolean = false,
) {
    val isConfigurationComplete: Boolean
        get() = merchantId.isNotBlank() && publicApiKey.isNotBlank()
}

/**
 * Drives the sample checkout: merchant configuration, device session and
 * tokenization through the Openpay SDK.
 *
 * @param openpayFactory indirection over building the SDK entry point so
 * tests can inject a mock.
 */
class CheckoutViewModel(
    private val openpayFactory: (OpenpayConfig) -> Openpay = { config -> Openpay(config) },
) : ViewModel() {

    private val mutableUiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = mutableUiState.asStateFlow()

    private var openpayInstance: Openpay? = null
    private var instanceConfig: OpenpayConfig? = null

    fun updateMerchantId(newValue: String) {
        mutableUiState.update { state -> state.copy(merchantId = newValue) }
    }

    fun updatePublicApiKey(newValue: String) {
        mutableUiState.update { state -> state.copy(publicApiKey = newValue) }
    }

    fun updateCountry(newValue: OpenpayCountry) {
        mutableUiState.update { state -> state.copy(country = newValue) }
    }

    fun updateProductionMode(enabled: Boolean) {
        mutableUiState.update { state -> state.copy(isProductionMode = enabled) }
    }

    /** Forces an SDK language, or returns to automatic resolution with null. */
    fun updateLanguage(language: OpenpayLanguage?) {
        OpenpayLocale.override = language
        mutableUiState.update { state -> state.copy(selectedLanguage = language) }
    }

    fun startDeviceSession(activity: Activity) {
        runWithOpenpay { openpay ->
            val deviceSessionId = openpay.setupDeviceSession(activity)
            mutableUiState.update { state ->
                state.copy(deviceSessionId = deviceSessionId, errorMessage = null)
            }
        }
    }

    fun tokenize(card: Card) {
        runWithOpenpay { openpay ->
            mutableUiState.update { state ->
                state.copy(isProcessing = true, createdToken = null, errorMessage = null)
            }
            viewModelScope.launch {
                val tokenResult = openpay.createToken(card)
                mutableUiState.update { state ->
                    tokenResult.fold(
                        onSuccess = { token ->
                            state.copy(isProcessing = false, createdToken = token)
                        },
                        onFailure = { failure ->
                            state.copy(isProcessing = false, errorMessage = describeError(failure))
                        },
                    )
                }
            }
        }
    }

    override fun onCleared() {
        openpayInstance?.shutdown()
    }

    private fun runWithOpenpay(action: (Openpay) -> Unit) {
        val state = mutableUiState.value
        if (!state.isConfigurationComplete) {
            mutableUiState.update { currentState ->
                currentState.copy(errorMessage = "Configure the merchant id and public key first")
            }
            return
        }
        action(resolveOpenpay(state))
    }

    private fun resolveOpenpay(state: CheckoutUiState): Openpay {
        val requestedConfig = OpenpayConfig(
            merchantId = state.merchantId.trim(),
            publicApiKey = state.publicApiKey.trim(),
            country = state.country,
            environment = if (state.isProductionMode) {
                OpenpayEnvironment.PRODUCTION
            } else {
                OpenpayEnvironment.SANDBOX
            },
        )
        val existingInstance = openpayInstance
        if (existingInstance != null && instanceConfig == requestedConfig) {
            return existingInstance
        }
        existingInstance?.shutdown()
        return openpayFactory(requestedConfig).also { newInstance ->
            openpayInstance = newInstance
            instanceConfig = requestedConfig
        }
    }

    private fun describeError(failure: Throwable): String =
        when (failure) {
            is OpenpayException.ValidationError ->
                "Invalid card fields: ${failure.validationResult.invalidFields}"
            is OpenpayException.ServiceError ->
                "Openpay rejected the request (${failure.errorCode}): ${failure.message}"
            is OpenpayException.ConnectionError ->
                "Could not reach Openpay, check your connection"
            else -> "Unexpected error: ${failure.message}"
        }
}
