package mx.dev1.openpay.sdk.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Test

class CardBrandTest {

    @Test
    fun `numbers starting with 4 are visa`() {
        assertEquals(CardBrand.VISA, CardBrand.fromCardNumber("4111111111111111"))
        assertEquals(CardBrand.VISA, CardBrand.fromCardNumber("4"))
    }

    @Test
    fun `numbers starting with 51 to 55 are mastercard`() {
        assertEquals(CardBrand.MASTERCARD, CardBrand.fromCardNumber("5105105105105100"))
        assertEquals(CardBrand.MASTERCARD, CardBrand.fromCardNumber("5555555555554444"))
    }

    @Test
    fun `numbers in the 2221 to 2720 series are mastercard`() {
        assertEquals(CardBrand.MASTERCARD, CardBrand.fromCardNumber("2221000000000009"))
        assertEquals(CardBrand.MASTERCARD, CardBrand.fromCardNumber("2720999999999996"))
    }

    @Test
    fun `numbers outside the 2 series edges are not mastercard`() {
        assertEquals(CardBrand.UNKNOWN, CardBrand.fromCardNumber("2220990000000000"))
        assertEquals(CardBrand.UNKNOWN, CardBrand.fromCardNumber("2721000000000000"))
    }

    @Test
    fun `numbers starting with 34 or 37 are american express`() {
        assertEquals(CardBrand.AMERICAN_EXPRESS, CardBrand.fromCardNumber("340000000000009"))
        assertEquals(CardBrand.AMERICAN_EXPRESS, CardBrand.fromCardNumber("370000000000002"))
    }

    @Test
    fun `unrecognized prefixes and empty input are unknown`() {
        assertEquals(CardBrand.UNKNOWN, CardBrand.fromCardNumber("6011000000000004"))
        assertEquals(CardBrand.UNKNOWN, CardBrand.fromCardNumber(""))
        assertEquals(CardBrand.UNKNOWN, CardBrand.fromCardNumber("abc"))
    }

    @Test
    fun `detection ignores spaces and dashes`() {
        assertEquals(CardBrand.VISA, CardBrand.fromCardNumber("4111 1111 1111 1111"))
        assertEquals(CardBrand.MASTERCARD, CardBrand.fromCardNumber("5105-1051-0510-5100"))
    }

    @Test
    fun `american express expects four digit security codes, the rest three`() {
        assertEquals(4, CardBrand.AMERICAN_EXPRESS.securityCodeLength)
        assertEquals(3, CardBrand.VISA.securityCodeLength)
        assertEquals(3, CardBrand.MASTERCARD.securityCodeLength)
        assertEquals(3, CardBrand.UNKNOWN.securityCodeLength)
    }
}
