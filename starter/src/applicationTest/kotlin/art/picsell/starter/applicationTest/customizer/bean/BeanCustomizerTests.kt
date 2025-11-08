package art.picsell.starter.applicationTest.customizer.bean

import art.picsell.starter.applicationTest.customizer.bean.BeanCustomizerApplication.Companion.BEAN_HEADER
import art.picsell.starter.applicationTest.customizer.bean.BeanCustomizerApplication.Companion.BEAN_HEADER_VALUE
import art.picsell.starter.applicationTest.customizer.bean.client.BeanCustomizerClient
import art.picsell.starter.applicationTest.testutils.AbstractStarterApplicationTest
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@SpringBootTest(classes = [BeanCustomizerApplication::class])
@ActiveProfiles("bean-customizer")
class BeanCustomizerTests : AbstractStarterApplicationTest(){

    @Autowired
    private lateinit var client: BeanCustomizerClient

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
