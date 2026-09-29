package mx.dev1.openpay.sdk.domain.validation

/**
 * Card fields that can fail validation, used to point errors to the right input.
 */
enum class CardField {
    HOLDER_NAME,
    CARD_NUMBER,
    EXPIRATION,
    SECURITY_CODE,
}
