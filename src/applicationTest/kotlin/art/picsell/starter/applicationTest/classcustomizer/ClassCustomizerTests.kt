package art.picsell.starter.applicationTest.classcustomizer

import art.picsell.starter.applicationTest.classcustomizer.client.ClassCustomizerClient
import art.picsell.starter.applicationTest.classcustomizer.customizer.ClassHeaderCustomizer
import art.picsell.starter.applicationTest.common.model.EchoRequest
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import kotlin.test.assertEquals

@SpringBootTest(classes = [ClassCustomizerApplication::class])
@ActiveProfiles("class-customizer")
class ClassCustomizerTests {

    @Autowired
    private lateinit var client: ClassCustomizerClient

    companion object {
        private val wireMockServer = WireMockServer(wireMockConfig().dynamicPort())

        @JvmStatic
        @BeforeAll
        fun startWireMock() {
            wireMockServer.start()
        }

        @JvmStatic
        @AfterAll
        fun stopWireMock() {
            wireMockServer.stop()
        }

        @JvmStatic
        @DynamicPropertySource
        fun applicationProperties(registry: DynamicPropertyRegistry) {
            registry.add("wiremock.port") { wireMockServer.port() }
        }
    }

    @BeforeEach
    fun resetWireMock() {
        wireMockServer.resetAll()
    }

    @Test
    fun `should include class customizer headers in GET`() {
        wireMockServer.stubFor(
            get(urlPathEqualTo("/api/greeting"))
                .withQueryParam("name", equalTo("Lev"))
                .withHeader(ClassHeaderCustomizer.CLASS_HEADER, equalTo(ClassHeaderCustomizer.CLASS_HEADER_VALUE))
                .willReturn(okJson("""{"message":"Hello, Lev!"}"""))
        )

        val response = client.greeting("Lev")

        assertEquals("Hello, Lev!", response.message)
    }

    @Test
    fun `should include class customizer headers in multiple endpoints`() {
        wireMockServer.stubFor(
            get(urlPathEqualTo("/api/items/7"))
                .withHeader(ClassHeaderCustomizer.CLASS_HEADER, equalTo(ClassHeaderCustomizer.CLASS_HEADER_VALUE))
                .willReturn(okJson("""{"id":7,"title":"Sample item"}"""))
        )
        wireMockServer.stubFor(
            post(urlPathEqualTo("/api/echo"))
                .withRequestBody(equalToJson("""{"value":"ping"}"""))
                .withHeader(ClassHeaderCustomizer.CLASS_HEADER, equalTo(ClassHeaderCustomizer.CLASS_HEADER_VALUE))
                .willReturn(okJson("""{"value":"ping","extra":"pong"}"""))
        )

        val item = client.getItem(7)
        val echo = client.echo(EchoRequest("ping"))

        assertEquals(7L, item.id)
        assertEquals("Sample item", item.title)
        assertEquals("ping", echo.value)
        assertEquals("pong", echo.extra)
    }
}
