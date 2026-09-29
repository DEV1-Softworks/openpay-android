package mx.dev1.openpay.sdk.domain.validation

import java.time.Clock
import java.time.YearMonth
import mx.dev1.openpay.sdk.domain.model.Card

/**
 * Pure validation rules for card data, equivalent to the legacy SDK's
 * CardValidator but with brand-aware security codes and clock injection
 * for deterministic tests.
 */
object CardValidator {

    private val CARD_NUMBER_LENGTH_RANGE = 12..19
    private val HOLDER_NAME_LENGTH_RANGE = 2..80
    private val HOLDER_NAME_PATTERN = Regex("""[\p{L}][\p{L}\s.'-]*""")
    private const val EXPIRATION_YEAR_CENTURY = 2000
    private const val MAXIMUM_YEARS_IN_FUTURE = 20L

    /**
     * A card number is valid when it only contains digits (spaces and dashes
     * are tolerated as separators), has a plausible length and passes Luhn.
     */
    fun isValidCardNumber(cardNumber: String): Boolean {
        val digits = normalizeCardNumber(cardNumber)
        if (digits.isEmpty() || digits.any { !it.isDigit() }) {
            return false
        }
        return digits.length in CARD_NUMBER_LENGTH_RANGE && passesLuhn(digits)
    }

    /** Holder names accept letters (including accents), spaces, dots, apostrophes and hyphens. */
    fun isValidHolderName(holderName: String): Boolean {
        val trimmedName = holderName.trim()
        return trimmedName.length in HOLDER_NAME_LENGTH_RANGE &&
            HOLDER_NAME_PATTERN.matches(trimmedName)
    }

    /**
     * The security code must be numeric and match the brand length:
     * 4 digits for American Express, 3 for every other brand.
     */
    fun isValidSecurityCode(securityCode: String, cardNumber: String = ""): Boolean {
        val expectedLength = CardBrand.fromCardNumber(cardNumber).securityCodeLength
        return securityCode.length == expectedLength && securityCode.all(Char::isDigit)
    }

    /**
     * The expiration is valid from the current month up to [MAXIMUM_YEARS_IN_FUTURE]
     * years ahead. Accepts two digit (27) or four digit (2027) years.
     */
    fun isValidExpiration(expirationMonth: Int, expirationYear: Int, clock: Clock = Clock.systemDefaultZone()): Boolean {
        if (expirationMonth !in 1..12 || expirationYear < 0) {
            return false
        }
        val fullYear = if (expirationYear < 100) EXPIRATION_YEAR_CENTURY + expirationYear else expirationYear
        val expiration = YearMonth.of(fullYear, expirationMonth)
        val currentMonth = YearMonth.now(clock)
        return !expiration.isBefore(currentMonth) &&
            expiration.isBefore(currentMonth.plusYears(MAXIMUM_YEARS_IN_FUTURE))
    }

    /** Validates the whole card and reports every field that failed. */
    fun validate(card: Card, clock: Clock = Clock.systemDefaultZone()): CardValidationResult {
        val invalidFields = buildSet {
            if (!isValidHolderName(card.holderName)) add(CardField.HOLDER_NAME)
            if (!isValidCardNumber(card.cardNumber)) add(CardField.CARD_NUMBER)
            if (!isValidExpiration(card.expirationMonth, card.expirationYear, clock)) add(CardField.EXPIRATION)
            if (!isValidSecurityCode(card.securityCode, card.cardNumber)) add(CardField.SECURITY_CODE)
        }
        return CardValidationResult(invalidFields)
    }

    private fun normalizeCardNumber(cardNumber: String): String =
        cardNumber.replace(" ", "").replace("-", "")

    private fun passesLuhn(digits: String): Boolean {
        var checksum = 0
        var shouldDoubleDigit = false
        for (i in digits.indices.reversed()) {
            var digitValue = digits[i] - '0'
            if (shouldDoubleDigit) {
                digitValue *= 2
                if (digitValue > 9) {
                    digitValue -= 9
                }
            }
            checksum += digitValue
            shouldDoubleDigit = !shouldDoubleDigit
        }
        return checksum % 10 == 0
    }
}
