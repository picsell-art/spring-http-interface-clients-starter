package art.picsell.starter.applicationTest.testutils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

abstract class AbstractStarterApplicationTest {
    @BeforeEach
    fun resetWireMock() {
        wireMockServer.resetAll()
    }
    
    companion object {
        @JvmStatic
        protected val wireMockServer = WireMockServer(wireMockConfig().dynamicPort())

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
}