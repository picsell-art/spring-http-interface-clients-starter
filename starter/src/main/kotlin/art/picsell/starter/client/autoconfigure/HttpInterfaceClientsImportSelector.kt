package art.picsell.starter.client.autoconfigure

import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.type.AnnotationMetadata

class HttpInterfaceClientsImportSelector : ImportBeanDefinitionRegistrar {
    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata, registry:
        BeanDefinitionRegistry
    ) {
        registry.registerBeanDefinition(
            "enableHttpInterfaceClientsMarker",
            RootBeanDefinition(EnableHttpInterfaceClientsMarker::class.java)
        )
    }
}
