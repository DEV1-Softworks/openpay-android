package mx.dev1.openpay.sdk.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CardTest {

    private fun cardWithExpiration(month: Int, year: Int) = Card(
        holderName = "Juan Perez Ramirez",
        cardNumber = "4111111111111111",
        expirationMonth = month,
        expirationYear = year,
        securityCode = "110",
    )

    @Test
    fun `single digit month is padded to two digits`() {
        assertEquals("03", cardWithExpiration(month = 3, year = 30).formattedExpirationMonth)
    }

    @Test
    fun `double digit month stays unchanged`() {
        assertEquals("12", cardWithExpiration(month = 12, year = 30).formattedExpirationMonth)
    }

    @Test
    fun `four digit year is reduced to its last two digits`() {
        assertEquals("30", cardWithExpiration(month = 1, year = 2030).formattedExpirationYear)
    }

    @Test
    fun `two digit year stays unchanged`() {
        assertEquals("27", cardWithExpiration(month = 1, year = 27).formattedExpirationYear)
    }

    @Test
    fun `single digit year is padded to two digits`() {
        assertEquals("09", cardWithExpiration(month = 1, year = 9).formattedExpirationYear)
    }
}
