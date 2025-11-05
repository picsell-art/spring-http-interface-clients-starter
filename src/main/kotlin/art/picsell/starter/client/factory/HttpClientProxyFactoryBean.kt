package art.picsell.starter.client.factory

import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import reactor.netty.http.client.HttpClient

class HttpClientProxyFactoryBean<T : Any>(
    private val serviceInterface: Class<T>,
    private val baseUrl: String,
    private val clientName: String
) : FactoryBean<T>, InitializingBean {

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder

    @Autowired(required = false)
    private var customizers: Map<String, WebClientCustomizer> = emptyMap()

    private lateinit var proxy: T

    override fun afterPropertiesSet() {
        val nettyClient = HttpClient.create()

        val builder = webClientBuilder
            .baseUrl(baseUrl)
            .clientConnector(ReactorClientHttpConnector(nettyClient))

        customizers["${clientName}Customizer"]?.customize(builder)

        val client = builder.build()
        val adapter = WebClientAdapter.create(client)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()
        proxy = factory.createClient(serviceInterface)
    }

    override fun getObject(): T = proxy
    override fun getObjectType(): Class<*> = serviceInterface
    override fun isSingleton(): Boolean = true
}
