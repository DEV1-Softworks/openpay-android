package mx.dev1.openpay.sdk.data.mapper

import mx.dev1.openpay.sdk.data.remote.dto.AddressDto
import mx.dev1.openpay.sdk.data.remote.dto.CardDto
import mx.dev1.openpay.sdk.data.remote.dto.TokenDto
import mx.dev1.openpay.sdk.domain.model.Address
import mx.dev1.openpay.sdk.domain.model.Card
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MapperTest {

    private val billingAddress = Address(
        line1 = "Av. Reforma 123",
        line2 = "Piso 4",
        postalCode = "06600",
        city = "Ciudad de Mexico",
        state = "CDMX",
        countryCode = "MX",
    )

    @Test
    fun `card maps to request dto with formatted expiration`() {
        val card = Card(
            holderName = "Juan Perez Ramirez",
            cardNumber = "4111111111111111",
            expirationMonth = 4,
            expirationYear = 2030,
            securityCode = "110",
            address = billingAddress,
        )

        val requestDto = card.toRequestDto()

        assertEquals("4111111111111111", requestDto.cardNumber)
        assertEquals("Juan Perez Ramirez", requestDto.holderName)
        assertEquals("04", requestDto.expirationMonth)
        assertEquals("30", requestDto.expirationYear)
        assertEquals("110", requestDto.securityCode)
        assertEquals("Av. Reforma 123", requestDto.address?.line1)
        assertEquals("06600", requestDto.address?.postalCode)
        assertNull(requestDto.brand)
        assertNull(requestDto.bankName)
    }

    @Test
    fun `token dto maps to domain token with card details`() {
        val tokenDto = TokenDto(
            tokenId = "tok_abc123",
            card = CardDto(
                cardNumber = "411111XXXXXX1111",
                holderName = "Juan Perez Ramirez",
                expirationMonth = "04",
                expirationYear = "30",
                brand = "visa",
                cardType = "debit",
                bankName = "Banamex",
                bankCode = "002",
                allowsCharges = true,
                allowsPayouts = false,
            ),
        )

        val token = tokenDto.toDomain()

        assertEquals("tok_abc123", token.tokenId)
        assertEquals("411111XXXXXX1111", token.card?.maskedCardNumber)
        assertEquals("visa", token.card?.brand)
        assertEquals("debit", token.card?.cardType)
        assertEquals("Banamex", token.card?.bankName)
        assertEquals(true, token.card?.allowsCharges)
        assertEquals(false, token.card?.allowsPayouts)
    }

    @Test
    fun `token dto without card maps to token without card`() {
        val token = TokenDto(tokenId = "tok_only").toDomain()

        assertEquals("tok_only", token.tokenId)
        assertNull(token.card)
    }

    @Test
    fun `address dto round trip keeps every field`() {
        val addressDto = billingAddress.toDto()
        val roundTrippedAddress = addressDto.toDomain()

        assertEquals(billingAddress, roundTrippedAddress)
    }

    @Test
    fun `address dto missing required fields maps to null`() {
        val incompleteAddressDto = AddressDto(line1 = "Av. Reforma 123", city = "CDMX")

        assertNull(incompleteAddressDto.toDomain())
    }
}
