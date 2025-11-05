package art.picsell.starter.client.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.web.service.annotation.HttpExchange

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@HttpExchange
annotation class HttpInterfaceClient(
    /**
     *  baseUrl = http-interface-clients.<name>.url
     *
     *  Name must be lowercase words separated by hyphen, e.g. demo-client.
     */
    val name: String,
    @get:AliasFor(annotation = HttpExchange::class, attribute = "url")
    val path: String = "",
    @get:AliasFor(annotation = HttpExchange::class, attribute = "accept")
    val accept: Array<String> = []
)
