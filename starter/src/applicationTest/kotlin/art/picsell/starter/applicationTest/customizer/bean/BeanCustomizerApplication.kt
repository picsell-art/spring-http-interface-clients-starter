package art.picsell.starter.applicationTest.customizer.bean

import art.picsell.starter.client.autoconfigure.EnableHttpInterfaceClients
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableHttpInterfaceClients
class BeanCustomizerApplication {

    @Bean("beanHeaderCustomizer")
    fun beanHeaderCustomizer(): WebClientCustomizer = WebClientCustomizer { builder ->
        builder.defaultHeader(BEAN_HEADER, BEAN_HEADER_VALUE)
    }

    companion object{
        const val BEAN_HEADER = "X-Bean-Customizer"
        const val BEAN_HEADER_VALUE = "bean-value"
    }
}
