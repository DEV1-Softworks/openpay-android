package mx.dev1.openpay.sdk.ui.state

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import mx.dev1.openpay.sdk.domain.validation.CardBrand
import mx.dev1.openpay.sdk.domain.validation.CardField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenpayCardFormStateTest {

    private val frozenClock: Clock =
        Clock.fixed(Instant.parse("2026-06-15T12:00:00Z"), ZoneOffset.UTC)

    private val formState = OpenpayCardFormState(frozenClock)

    private fun fillValidCard() {
        formState.updateHolderName("Juan Pérez Ramírez")
        formState.updateCardNumber("4111111111111111")
        formState.updateExpiration("1230")
        formState.updateSecurityCode("110")
    }

    @Test
    fun `card number input keeps digits only and caps length`() {
        formState.updateCardNumber("4111 1111-1111 1111 99999")

        assertEquals("4111111111111111999", formState.cardNumber)
    }

    @Test
    fun `expiration input keeps four digits maximum`() {
        formState.updateExpiration("12/30/99")

        assertEquals("1230", formState.expiration)
    }

    @Test
    fun `security code length follows the detected brand`() {
        formState.updateCardNumber("378282246310005")
        formState.updateSecurityCode("12345")
        assertEquals("1234", formState.securityCode)

        formState.updateCardNumber("4111111111111111")
        formState.updateSecurityCode("12345")
        assertEquals("123", formState.securityCode)
    }

    @Test
    fun `brand detection follows the card number`() {
        formState.updateCardNumber("5555")

        assertEquals(CardBrand.MASTERCARD, formState.detectedBrand)
    }

    @Test
    fun `validating a complete card returns it`() {
        fillValidCard()

        val validatedCard = formState.validate()

        assertNotNull(validatedCard)
        assertEquals("4111111111111111", checkNotNull(validatedCard).cardNumber)
        assertEquals(12, validatedCard.expirationMonth)
        assertEquals(30, validatedCard.expirationYear)
        assertTrue(checkNotNull(formState.validationResult).isValid)
    }

    @Test
    fun `validating an incomplete card reports the failing fields`() {
        fillValidCard()
        formState.updateExpiration("12")

        val validatedCard = formState.validate()

        assertNull(validatedCard)
        assertTrue(formState.isFieldInvalid(CardField.EXPIRATION))
        assertFalse(formState.isFieldInvalid(CardField.CARD_NUMBER))
    }

    @Test
    fun `editing a field clears its error but keeps the others`() {
        formState.validate()
        assertTrue(formState.isFieldInvalid(CardField.CARD_NUMBER))
        assertTrue(formState.isFieldInvalid(CardField.EXPIRATION))

        formState.updateCardNumber("4111111111111111")

        assertFalse(formState.isFieldInvalid(CardField.CARD_NUMBER))
        assertTrue(formState.isFieldInvalid(CardField.EXPIRATION))
    }
}
