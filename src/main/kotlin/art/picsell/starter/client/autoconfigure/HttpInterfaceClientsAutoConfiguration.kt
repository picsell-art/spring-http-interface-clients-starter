package art.picsell.starter.client.autoconfigure

import art.picsell.starter.client.properties.HttpInterfaceClientsProperties
import art.picsell.starter.client.registrar.HttpClientsRegistrar
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration

@AutoConfiguration
@AutoConfigureAfter(WebClientAutoConfiguration::class)
@ConditionalOnClass(WebClient::class)
@ConditionalOnBean(WebClient.Builder::class)
@EnableConfigurationProperties(HttpInterfaceClientsProperties::class)
@Import(HttpClientsRegistrar::class)
class HttpInterfaceClientsAutoConfiguration
