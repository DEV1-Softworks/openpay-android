package mx.dev1.openpay.sdk.domain.model

/**
 * Card data captured from the user that will be exchanged for a token.
 *
 * @property holderName Name printed on the card.
 * @property cardNumber Card number without spaces or dashes.
 * @property expirationMonth Expiration month between 1 and 12.
 * @property expirationYear Expiration year, either two digits (25) or four digits (2025).
 * @property securityCode CVV2/CVC2 security code, 3 digits (4 for American Express).
 */
data class Card(
    val holderName: String,
    val cardNumber: String,
    val expirationMonth: Int,
    val expirationYear: Int,
    val securityCode: String,
    val address: Address? = null,
) {

    /** Expiration month as the two digit text expected by the Openpay API. */
    val formattedExpirationMonth: String
        get() = expirationMonth.toString().padStart(2, '0')

    /** Expiration year reduced to its last two digits, as expected by the Openpay API. */
    val formattedExpirationYear: String
        get() = (expirationYear % 100).toString().padStart(2, '0')
}
