package mx.dev1.openpay.sdk.domain.validation

/**
 * Outcome of validating a whole card. When [invalidFields] is empty the card
 * is ready to be tokenized.
 */
data class CardValidationResult(val invalidFields: Set<CardField>) {

    val isValid: Boolean
        get() = invalidFields.isEmpty()

    fun isFieldValid(field: CardField): Boolean = field !in invalidFields
}
