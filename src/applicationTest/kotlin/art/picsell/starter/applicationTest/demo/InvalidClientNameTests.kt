package art.picsell.starter.applicationTest.demo

import art.picsell.starter.applicationTest.validation.invalid.InvalidConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import kotlin.test.assertTrue

class InvalidClientNameTests {

    @Test
    fun `should fail to register client with invalid name`() {
        val exception = assertThrows<IllegalArgumentException> {
            AnnotationConfigApplicationContext(InvalidConfig::class.java)
        }

        assertTrue(exception.message!!.contains("Expected lowercase words separated"))
    }
}
