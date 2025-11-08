package art.picsell.starter.client.registrar

import art.picsell.starter.client.autoconfigure.EnableHttpInterfaceClients
import art.picsell.starter.client.annotation.HttpInterfaceClient
import art.picsell.starter.client.factory.HttpClientProxyFactoryBean
import art.picsell.starter.client.properties.HttpInterfaceClientsProperties
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.boot.autoconfigure.AutoConfigurationPackages
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.context.EnvironmentAware
import org.springframework.context.ResourceLoaderAware
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.env.Environment
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.util.ClassUtils

class HttpClientsRegistrar :
    ImportBeanDefinitionRegistrar,
    ResourceLoaderAware,
    EnvironmentAware,
    BeanFactoryAware {

    private lateinit var resourceLoader: ResourceLoader
    private lateinit var readerFactory: MetadataReaderFactory
    private lateinit var env: Environment
    private var beanFactory: BeanFactory? = null

    override fun setResourceLoader(loader: ResourceLoader) {
        resourceLoader = loader
        readerFactory = CachingMetadataReaderFactory(loader)
    }

    override fun setEnvironment(environment: Environment) {
        env = environment
    }

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry
    ) {
        val basePackages = resolveBasePackages(importingClassMetadata)
        if (basePackages.isEmpty()) {
            return
        }

        val resolver = PathMatchingResourcePatternResolver(resourceLoader)
        val binder = Binder.get(env)
        val properties = binder
            .bind(HttpInterfaceClientsProperties.PREFIX, Bindable.of(HttpInterfaceClientsProperties::class.java))
            .orElseGet(::HttpInterfaceClientsProperties)
        val classLoader = resourceLoader.classLoader ?: Thread.currentThread().contextClassLoader

        basePackages.forEach { pkg ->
            val pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    pkg.replace('.', '/') +
                    "/**/*.class"
            resolver.getResources(pattern).forEach { resource ->
                if (!resource.isReadable) return@forEach
                val metadataReader = readerFactory.getMetadataReader(resource)
                val meta = metadataReader.annotationMetadata
                if (meta.hasAnnotation(HttpInterfaceClient::class.java.name)) {
                    val className = metadataReader.classMetadata.className

                    @Suppress("UNCHECKED_CAST")
                    val iface = ClassUtils.forName(className, classLoader) as Class<Any>
                    val ann = iface.getAnnotation(HttpInterfaceClient::class.java)
                    val clientName = ann.name
                    HttpInterfaceClientsProperties.validateName(clientName)
                    val clientProps = properties.getRequired(clientName)
                    val beanName = clientName

                    val beanDef = BeanDefinitionBuilder
                        .genericBeanDefinition(HttpClientProxyFactoryBean::class.java)
                        .addConstructorArgValue(iface)
                        .addConstructorArgValue(clientProps.url)
                        .addConstructorArgValue(clientProps.customizerBean)
                        .addConstructorArgValue(clientProps.customizerClass)
                        .beanDefinition

                    registry.registerBeanDefinition(beanName, beanDef)
                }
            }
        }
    }

    private fun resolveBasePackages(metadata: AnnotationMetadata): List<String> {
        val attributes = metadata.getAnnotationAttributes(EnableHttpInterfaceClients::class.java.name)
        if (attributes != null) {
            val packages = (attributes["basePackages"] as Array<String>?)
                ?.filter { it.isNotBlank() }
                .orEmpty()
            if (packages.isNotEmpty()) {
                return packages
            }
            return listOf(ClassUtils.getPackageName(metadata.className))
        }

        val factory = beanFactory
        if (factory != null && AutoConfigurationPackages.has(factory)) {
            return AutoConfigurationPackages.get(factory)
        }

        return listOf(ClassUtils.getPackageName(metadata.className))
    }
}
