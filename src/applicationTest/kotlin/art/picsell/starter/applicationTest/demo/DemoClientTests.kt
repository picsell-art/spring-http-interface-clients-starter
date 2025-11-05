package art.picsell.starter.applicationTest.demo

import art.picsell.starter.applicationTest.demo.client.DemoClient
import art.picsell.starter.applicationTest.demo.model.EchoRequest
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
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import kotlin.test.assertEquals

@SpringBootTest(classes = [DemoApplication::class])
class DemoClientTests {

    @Autowired
    private lateinit var demoClient: DemoClient

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
    fun `should fetch greeting message`() {
        wireMockServer.stubFor(
            get(urlPathEqualTo("/api/greeting"))
                .withQueryParam("name", equalTo("Lev"))
                .willReturn(okJson("""{"message":"Hello, Lev!"}"""))
        )

        val response = demoClient.greeting("Lev")

        assertEquals("Hello, Lev!", response.message)
    }

    @Test
    fun `should fetch item and echo payload`() {
        wireMockServer.stubFor(
            get(urlPathEqualTo("/api/items/7"))
                .willReturn(okJson("""{"id":7,"title":"Sample item"}"""))
        )
        wireMockServer.stubFor(
            post(urlPathEqualTo("/api/echo"))
                .withRequestBody(equalToJson("""{"value":"ping"}"""))
                .willReturn(okJson("""{"value":"ping","extra":"pong"}"""))
        )

        val item = demoClient.getItem(7)
        val echo = demoClient.echo(EchoRequest("ping"))

        assertEquals(7L, item.id)
        assertEquals("Sample item", item.title)
        assertEquals("ping", echo.value)
        assertEquals("pong", echo.extra)
    }
}
