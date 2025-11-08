package art.picsell.gradle.httpinterface

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class HttpInterfaceClientStubsExtension @Inject constructor(objects: ObjectFactory) {
    val enabled: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(true)

    val stubClassSuffix: Property<String> = objects.property(String::class.java)
        .convention("HttpInterfaceClientStub")

    val componentAnnotation: Property<String> = objects.property(String::class.java)
        .convention("org.springframework.stereotype.Component")

    val componentBeanNamePrefix: Property<String> = objects.property(String::class.java)
        .convention("httpInterfaceClient:")
}
