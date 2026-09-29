package mx.dev1.openpay.sdk.domain.validation

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import mx.dev1.openpay.sdk.domain.model.Card
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardValidatorTest {

    /** Frozen at 2026-06-15 UTC so expiration tests are deterministic. */
    private val frozenClock: Clock =
        Clock.fixed(Instant.parse("2026-06-15T12:00:00Z"), ZoneOffset.UTC)

    // Card number

    @Test
    fun `valid numbers of every brand pass`() {
        assertTrue(CardValidator.isValidCardNumber("4111111111111111"))
        assertTrue(CardValidator.isValidCardNumber("5555555555554444"))
        assertTrue(CardValidator.isValidCardNumber("378282246310005"))
    }

    @Test
    fun `separators are tolerated in card numbers`() {
        assertTrue(CardValidator.isValidCardNumber("4111 1111 1111 1111"))
        assertTrue(CardValidator.isValidCardNumber("4111-1111-1111-1111"))
    }

    @Test
    fun `luhn failures are rejected`() {
        assertFalse(CardValidator.isValidCardNumber("4111111111111112"))
    }

    @Test
    fun `non numeric and empty input is rejected`() {
        assertFalse(CardValidator.isValidCardNumber(""))
        assertFalse(CardValidator.isValidCardNumber("4111a11111111111"))
    }

    @Test
    fun `too short and too long numbers are rejected`() {
        assertFalse(CardValidator.isValidCardNumber("41111111111"))
        assertFalse(CardValidator.isValidCardNumber("41111111111111111111"))
    }

    // Holder name

    @Test
    fun `names with accents and common punctuation pass`() {
        assertTrue(CardValidator.isValidHolderName("Juan Pérez Ramírez"))
        assertTrue(CardValidator.isValidHolderName("Éloïse D'Angelo-Smith"))
        assertTrue(CardValidator.isValidHolderName("João da Silva Jr."))
    }

    @Test
    fun `blank short and symbol names are rejected`() {
        assertFalse(CardValidator.isValidHolderName(""))
        assertFalse(CardValidator.isValidHolderName("   "))
        assertFalse(CardValidator.isValidHolderName("J"))
        assertFalse(CardValidator.isValidHolderName("Juan <script>"))
        assertFalse(CardValidator.isValidHolderName("1234"))
    }

    // Security code

    @Test
    fun `three digit codes pass for visa and mastercard`() {
        assertTrue(CardValidator.isValidSecurityCode("110", "4111111111111111"))
        assertTrue(CardValidator.isValidSecurityCode("999", "5555555555554444"))
    }

    @Test
    fun `american express requires four digits`() {
        assertTrue(CardValidator.isValidSecurityCode("1234", "378282246310005"))
        assertFalse(CardValidator.isValidSecurityCode("123", "378282246310005"))
    }

    @Test
    fun `wrong length or non numeric codes are rejected`() {
        assertFalse(CardValidator.isValidSecurityCode("12", "4111111111111111"))
        assertFalse(CardValidator.isValidSecurityCode("1234", "4111111111111111"))
        assertFalse(CardValidator.isValidSecurityCode("12a", "4111111111111111"))
        assertFalse(CardValidator.isValidSecurityCode("", "4111111111111111"))
    }

    // Expiration

    @Test
    fun `current month and future dates pass`() {
        assertTrue(CardValidator.isValidExpiration(6, 26, frozenClock))
        assertTrue(CardValidator.isValidExpiration(7, 26, frozenClock))
        assertTrue(CardValidator.isValidExpiration(1, 2030, frozenClock))
    }

    @Test
    fun `past dates are rejected`() {
        assertFalse(CardValidator.isValidExpiration(5, 26, frozenClock))
        assertFalse(CardValidator.isValidExpiration(12, 2025, frozenClock))
    }

    @Test
    fun `invalid months are rejected`() {
        assertFalse(CardValidator.isValidExpiration(0, 30, frozenClock))
        assertFalse(CardValidator.isValidExpiration(13, 30, frozenClock))
        assertFalse(CardValidator.isValidExpiration(-1, 30, frozenClock))
    }

    @Test
    fun `unreasonably distant years are rejected`() {
        assertFalse(CardValidator.isValidExpiration(1, 2047, frozenClock))
        assertFalse(CardValidator.isValidExpiration(1, -5, frozenClock))
    }

    // Whole card validation

    private val validCard = Card(
        holderName = "Juan Pérez Ramírez",
        cardNumber = "4111111111111111",
        expirationMonth = 12,
        expirationYear = 30,
        securityCode = "110",
    )

    @Test
    fun `a fully valid card reports no invalid fields`() {
        val result = CardValidator.validate(validCard, frozenClock)

        assertTrue(result.isValid)
        assertTrue(CardField.entries.all(result::isFieldValid))
    }

    @Test
    fun `every broken field is reported`() {
        val brokenCard = validCard.copy(
            holderName = "X",
            cardNumber = "1234",
            expirationMonth = 13,
            securityCode = "9",
        )

        val result = CardValidator.validate(brokenCard, frozenClock)

        assertFalse(result.isValid)
        assertEquals(
            setOf(
                CardField.HOLDER_NAME,
                CardField.CARD_NUMBER,
                CardField.EXPIRATION,
                CardField.SECURITY_CODE,
            ),
            result.invalidFields,
        )
    }

    @Test
    fun `a single broken field leaves the rest valid`() {
        val cardWithBadCode = validCard.copy(securityCode = "12")

        val result = CardValidator.validate(cardWithBadCode, frozenClock)

        assertFalse(result.isValid)
        assertEquals(setOf(CardField.SECURITY_CODE), result.invalidFields)
        assertTrue(result.isFieldValid(CardField.CARD_NUMBER))
    }
}
