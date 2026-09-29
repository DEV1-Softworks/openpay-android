package mx.dev1.openpay.sdk.ui.input

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpirationVisualTransformationTest {

    private val transformation = ExpirationVisualTransformation()

    private fun transform(digits: String) = transformation.filter(AnnotatedString(digits))

    @Test
    fun `four digits show as month slash year`() {
        assertEquals("12/30", transform("1230").text.text)
    }

    @Test
    fun `one or two digits show without separator`() {
        assertEquals("1", transform("1").text.text)
        assertEquals("12", transform("12").text.text)
    }

    @Test
    fun `three digits show the separator`() {
        assertEquals("12/3", transform("123").text.text)
    }

    @Test
    fun `offsets map across the separator`() {
        val mapping = transform("1230").offsetMapping

        assertEquals(2, mapping.originalToTransformed(2))
        assertEquals(4, mapping.originalToTransformed(3))
        assertEquals(5, mapping.originalToTransformed(4))
        assertEquals(2, mapping.transformedToOriginal(2))
        assertEquals(3, mapping.transformedToOriginal(4))
        assertEquals(4, mapping.transformedToOriginal(5))
    }

    @Test
    fun `round trip stays consistent`() {
        val mapping = transform("1230").offsetMapping

        for (originalOffset in 0..4) {
            assertEquals(
                originalOffset,
                mapping.transformedToOriginal(mapping.originalToTransformed(originalOffset)),
            )
        }
    }
}
