package art.picsell.starter.applicationTest.customizer.classname

import art.picsell.starter.applicationTest.customizer.classname.customizer.ClassHeaderCustomizer
import art.picsell.starter.client.autoconfigure.EnableHttpInterfaceClients
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableHttpInterfaceClients
class ClassCustomizerApplication {

    @Bean("beanHeaderCustomizer")
    fun beanHeaderCustomizer(): WebClientCustomizer = ClassHeaderCustomizer()
}
