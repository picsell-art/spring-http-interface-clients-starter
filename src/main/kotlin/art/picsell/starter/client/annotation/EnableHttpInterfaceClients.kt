package art.picsell.starter.client.annotation

import art.picsell.starter.client.properties.HttpInterfaceClientsProperties
import art.picsell.starter.client.registrar.HttpClientsRegistrar
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@EnableConfigurationProperties(HttpInterfaceClientsProperties::class)
@Import(HttpClientsRegistrar::class)
annotation class EnableHttpInterfaceClients(vararg val basePackages: String = [])
