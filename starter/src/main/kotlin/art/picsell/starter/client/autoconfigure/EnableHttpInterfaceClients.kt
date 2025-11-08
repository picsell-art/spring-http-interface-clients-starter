package art.picsell.starter.client.autoconfigure

import art.picsell.starter.client.properties.HttpInterfaceClientsProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@EnableConfigurationProperties(HttpInterfaceClientsProperties::class)
@Import(HttpInterfaceClientsImportSelector::class)
annotation class EnableHttpInterfaceClients(
    vararg val basePackages: String = []
)
