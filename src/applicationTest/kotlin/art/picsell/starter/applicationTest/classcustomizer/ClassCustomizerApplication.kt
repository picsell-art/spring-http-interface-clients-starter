package art.picsell.starter.applicationTest.classcustomizer

import art.picsell.starter.applicationTest.classcustomizer.customizer.ClassHeaderCustomizer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ClassCustomizerApplication {

    @Bean("beanHeaderCustomizer")
    fun beanHeaderCustomizer(): WebClientCustomizer = ClassHeaderCustomizer()
}
