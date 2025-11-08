package art.picsell.starter.applicationTest.validation.invalid

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.annotation.AnnotationConfigApplicationContext

class InvalidClientNameTests {

    @Test
    fun `should fail to register client with invalid name`() {
        val exception = assertThrows<IllegalArgumentException> {
            AnnotationConfigApplicationContext(InvalidConfigApplication::class.java)
        }

        assertTrue(exception.message!!.contains("Expected lowercase words separated"))
    }
}