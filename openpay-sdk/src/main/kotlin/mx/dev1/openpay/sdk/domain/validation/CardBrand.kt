package mx.dev1.openpay.sdk.domain.validation

/**
 * Card brands recognized by the SDK, with the security code length each one uses.
 */
enum class CardBrand(val securityCodeLength: Int) {
    VISA(securityCodeLength = 3),
    MASTERCARD(securityCodeLength = 3),
    AMERICAN_EXPRESS(securityCodeLength = 4),
    UNKNOWN(securityCodeLength = 3);

    companion object {

        private val MASTERCARD_LEGACY_PREFIXES = 51..55
        private val MASTERCARD_2_SERIES_PREFIXES = 2221..2720
        private val AMERICAN_EXPRESS_PREFIXES = setOf("34", "37")

        private val BRAND_NAMES = mapOf(
            "visa" to VISA,
            "mastercard" to MASTERCARD,
            "master_card" to MASTERCARD,
            "american_express" to AMERICAN_EXPRESS,
            "amex" to AMERICAN_EXPRESS,
        )

        /**
         * Detects the brand from the card number prefix (IIN). Works with
         * partial input, so UIs can show the brand while the user types.
         */
        fun fromCardNumber(cardNumber: String): CardBrand {
            val digits = cardNumber.filter(Char::isDigit)
            return when {
                digits.isEmpty() -> UNKNOWN
                digits.startsWith("4") -> VISA
                digits.take(2).toIntOrNull() in MASTERCARD_LEGACY_PREFIXES -> MASTERCARD
                digits.take(4).toIntOrNull() in MASTERCARD_2_SERIES_PREFIXES -> MASTERCARD
                digits.take(2) in AMERICAN_EXPRESS_PREFIXES -> AMERICAN_EXPRESS
                else -> UNKNOWN
            }
        }

        /**
         * Maps the brand name echoed by the Openpay API (for example "visa"
         * or "american_express") to a [CardBrand], ignoring case.
         */
        fun fromBrandName(brandName: String?): CardBrand =
            BRAND_NAMES[brandName?.trim()?.lowercase()] ?: UNKNOWN
    }
}
