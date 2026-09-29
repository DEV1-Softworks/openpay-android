package mx.dev1.openpay.sdk.ui.input

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Test

class CardNumberVisualTransformationTest {

    private val transformation = CardNumberVisualTransformation()

    private fun transform(digits: String) = transformation.filter(AnnotatedString(digits))

    @Test
    fun `sixteen digit numbers show groups of four`() {
        assertEquals("4111 1111 1111 1111", transform("4111111111111111").text.text)
    }

    @Test
    fun `american express numbers show 4-6-5 groups`() {
        assertEquals("3782 822463 10005", transform("378282246310005").text.text)
    }

    @Test
    fun `partial input only shows completed separators`() {
        assertEquals("4111", transform("4111").text.text)
        assertEquals("4111 1", transform("41111").text.text)
        assertEquals("", transform("").text.text)
    }

    @Test
    fun `original offsets map to transformed offsets`() {
        val mapping = transform("4111111111111111").offsetMapping

        assertEquals(0, mapping.originalToTransformed(0))
        assertEquals(3, mapping.originalToTransformed(3))
        assertEquals(5, mapping.originalToTransformed(4))
        assertEquals(10, mapping.originalToTransformed(8))
        assertEquals(19, mapping.originalToTransformed(16))
    }

    @Test
    fun `transformed offsets map back to original offsets`() {
        val mapping = transform("4111111111111111").offsetMapping

        assertEquals(0, mapping.transformedToOriginal(0))
        assertEquals(4, mapping.transformedToOriginal(4))
        assertEquals(4, mapping.transformedToOriginal(5))
        assertEquals(8, mapping.transformedToOriginal(10))
        assertEquals(16, mapping.transformedToOriginal(19))
    }

    @Test
    fun `amex offsets map around both separators`() {
        val mapping = transform("378282246310005").offsetMapping

        assertEquals(5, mapping.originalToTransformed(4))
        assertEquals(12, mapping.originalToTransformed(10))
        assertEquals(17, mapping.originalToTransformed(15))
        assertEquals(10, mapping.transformedToOriginal(11))
        assertEquals(15, mapping.transformedToOriginal(17))
    }

    @Test
    fun `round trip stays consistent for every cursor position`() {
        val digits = "4111111111111111"
        val mapping = transform(digits).offsetMapping

        for (originalOffset in 0..digits.length) {
            val roundTrippedOffset =
                mapping.transformedToOriginal(mapping.originalToTransformed(originalOffset))
            assertEquals(originalOffset, roundTrippedOffset)
        }
    }
}
