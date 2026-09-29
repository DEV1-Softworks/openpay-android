package mx.dev1.openpay.sdk.antifraud

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceSessionIdGeneratorTest {

    private val sessionIdGenerator = DeviceSessionIdGenerator()

    @Test
    fun `session ids have 32 hexadecimal characters`() {
        val sessionId = sessionIdGenerator.generate()

        assertEquals(32, sessionId.length)
        assertTrue(sessionId.all { character -> character in "0123456789abcdef" })
    }

    @Test
    fun `session ids are unique`() {
        val generatedIds = List(100) { sessionIdGenerator.generate() }.toSet()

        assertEquals(100, generatedIds.size)
    }
}
