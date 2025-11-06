package art.picsell.starter.applicationTest.beancustomizer

import art.picsell.starter.applicationTest.beancustomizer.client.BeanCustomizerClient
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.okJson
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

@SpringBootTest(classes = [BeanCustomizerApplication::class])
@ActiveProfiles("bean-customizer")
class BeanCustomizerTests {

    @Autowired
    private lateinit var client: BeanCustomizerClient

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
    fun `should include bean customizer header`() {
        wireMockServer.stubFor(
            get(urlPathEqualTo("/api/bean"))
                .withHeader(BEAN_HEADER, equalTo(BEAN_HEADER_VALUE))
                .willReturn(okJson("""{"message":"Bean header works"}"""))
        )

        val response = client.beanGreeting()

        assertEquals("Bean header works", response.message)
    }
}
