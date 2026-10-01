package mx.dev1.openpay.sdk

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenpaySdkTest {

    @Test
    fun `version follows semantic versioning`() {
        val semanticVersionPattern = Regex("""\d+\.\d+\.\d+""")
        assertTrue(OpenpaySdk.VERSION.matches(semanticVersionPattern))
    }

    @Test
    fun `user agent contains sdk name and version`() {
        assertEquals("openpay-android/${OpenpaySdk.VERSION}", OpenpaySdk.userAgent)
    }

    @Test
    fun `building a user agent with a blank version fails`() {
        assertThrows(IllegalArgumentException::class.java) {
            OpenpaySdk.buildUserAgent(" ")
        }
    }
}
