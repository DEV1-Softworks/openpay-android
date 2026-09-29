package mx.dev1.openpay.sdk.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.Clock
import mx.dev1.openpay.sdk.domain.model.Card
import mx.dev1.openpay.sdk.domain.validation.CardBrand
import mx.dev1.openpay.sdk.domain.validation.CardField
import mx.dev1.openpay.sdk.domain.validation.CardValidationResult
import mx.dev1.openpay.sdk.domain.validation.CardValidator

/**
 * State holder for the Openpay card form. Field values are kept digits-only;
 * visual formatting happens in the composables.
 *
 * Card data is intentionally NOT persisted across process death: it is
 * sensitive payment information, so the state uses [remember] semantics only.
 */
@Stable
class OpenpayCardFormState internal constructor(private val clock: Clock) {

    var holderName: String by mutableStateOf("")
        private set
    var cardNumber: String by mutableStateOf("")
        private set
    var expiration: String by mutableStateOf("")
        private set
    var securityCode: String by mutableStateOf("")
        private set

    var validationResult: CardValidationResult? by mutableStateOf(null)
        private set

    val detectedBrand: CardBrand
        get() = CardBrand.fromCardNumber(cardNumber)

    fun updateHolderName(newValue: String) {
        holderName = newValue.take(MAXIMUM_HOLDER_NAME_LENGTH)
        clearFieldError(CardField.HOLDER_NAME)
    }

    fun updateCardNumber(newValue: String) {
        cardNumber = newValue.filter(Char::isDigit).take(MAXIMUM_CARD_NUMBER_LENGTH)
        clearFieldError(CardField.CARD_NUMBER)
    }

    /** Expects digits in MMYY order, as typed through the expiration field. */
    fun updateExpiration(newValue: String) {
        expiration = newValue.filter(Char::isDigit).take(EXPIRATION_LENGTH)
        clearFieldError(CardField.EXPIRATION)
    }

    fun updateSecurityCode(newValue: String) {
        securityCode = newValue.filter(Char::isDigit).take(detectedBrand.securityCodeLength)
        clearFieldError(CardField.SECURITY_CODE)
    }

    fun isFieldInvalid(field: CardField): Boolean =
        validationResult?.isFieldValid(field) == false

    /**
     * Validates every field. Returns the [Card] ready for tokenization when
     * everything is valid, or null after publishing the failing fields in
     * [validationResult].
     */
    fun validate(): Card? {
        val candidateCard = buildCard()
        val result = CardValidator.validate(candidateCard, clock)
        validationResult = result
        return if (result.isValid) candidateCard else null
    }

    private fun buildCard(): Card =
        Card(
            holderName = holderName.trim(),
            cardNumber = cardNumber,
            expirationMonth = expiration.take(2).toIntOrNull() ?: INVALID_DATE_PART,
            expirationYear = if (expiration.length == EXPIRATION_LENGTH) {
                expiration.drop(2).toIntOrNull() ?: INVALID_DATE_PART
            } else {
                INVALID_DATE_PART
            },
            securityCode = securityCode,
        )

    private fun clearFieldError(field: CardField) {
        val currentResult = validationResult ?: return
        validationResult = CardValidationResult(currentResult.invalidFields - field)
    }

    private companion object {
        const val MAXIMUM_HOLDER_NAME_LENGTH = 80
        const val MAXIMUM_CARD_NUMBER_LENGTH = 19
        const val EXPIRATION_LENGTH = 4
        const val INVALID_DATE_PART = -1
    }
}

/** Remembers an [OpenpayCardFormState] for the composition. */
@Composable
fun rememberOpenpayCardFormState(): OpenpayCardFormState =
    remember { OpenpayCardFormState(Clock.systemDefaultZone()) }
