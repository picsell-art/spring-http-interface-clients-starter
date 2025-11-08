package art.picsell.starter.applicationTest.customizer.classname.customizer

import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.web.reactive.function.client.WebClient

class ClassHeaderCustomizer : WebClientCustomizer {
    override fun customize(builder: WebClient.Builder) {
        builder.defaultHeader(CLASS_HEADER, CLASS_HEADER_VALUE)
    }

    companion object {
        const val CLASS_HEADER = "X-Class-Customizer"
        const val CLASS_HEADER_VALUE = "class-value"
    }
}
