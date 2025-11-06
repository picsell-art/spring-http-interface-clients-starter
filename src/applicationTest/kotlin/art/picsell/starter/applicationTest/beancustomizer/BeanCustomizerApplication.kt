package art.picsell.starter.applicationTest.beancustomizer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.context.annotation.Bean

const val BEAN_HEADER = "X-Bean-Customizer"
const val BEAN_HEADER_VALUE = "bean-value"

@SpringBootApplication
class BeanCustomizerApplication {

    @Bean("beanHeaderCustomizer")
    fun beanHeaderCustomizer(): WebClientCustomizer = WebClientCustomizer { builder ->
        builder.defaultHeader(BEAN_HEADER, BEAN_HEADER_VALUE)
    }
}
