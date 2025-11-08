package art.picsell.starter.client.properties

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.validation.annotation.Validated
import org.springframework.util.ClassUtils

@ConfigurationProperties(HttpInterfaceClientsProperties.PREFIX)
@Validated
class HttpInterfaceClientsProperties :
    LinkedHashMap<String, HttpInterfaceClientsProperties.ClientProperties>() {

    override fun put(key: String, value: ClientProperties): ClientProperties? {
        validateName(key)
        return super.put(key, value)
    }

    override fun putAll(from: Map<out String, ClientProperties>) {
        from.keys.forEach(::validateName)
        super.putAll(from)
    }

    fun getRequired(name: String): ClientProperties =
        this[name] ?: error("Missing configuration for http-interface-clients.$name")

    private fun validateName(name: String) = Companion.validateName(name)

    class ClientProperties : LinkedHashMap<String, Any>() {
        @get:NotBlank
        var url: String
            get() = (this[URL_KEY] as? String)
                ?: error("Missing http-interface client property '$URL_KEY'")
            set(value) {
                this[URL_KEY] = value
            }

        var customizerBean: String?
            get() = this[CUSTOMIZER_BEAN_KEY] as? String
            set(value) {
                if (value == null) {
                    remove(CUSTOMIZER_BEAN_KEY)
                } else {
                    this[CUSTOMIZER_BEAN_KEY] = value
                }
            }

        var customizerClass: Class<out WebClientCustomizer>?
            get() {
                val value = this[CUSTOMIZER_CLASS_KEY]
                return when (value) {
                    null -> null
                    is Class<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        value as Class<out WebClientCustomizer>
                    }
                    is String -> {
                        val resolved = ClassUtils.resolveClassName(
                            value,
                            ClassUtils.getDefaultClassLoader()
                        )
                        @Suppress("UNCHECKED_CAST")
                        resolved as Class<out WebClientCustomizer>
                    }
                    else -> null
                }
            }
            set(value) {
                if (value == null) {
                    remove(CUSTOMIZER_CLASS_KEY)
                } else {
                    this[CUSTOMIZER_CLASS_KEY] = value
                }
            }

        fun additional(key: String): Any? = this[key]

        companion object {
            private const val URL_KEY = "url"
            private const val CUSTOMIZER_BEAN_KEY = "customizer-bean"
            private const val CUSTOMIZER_CLASS_KEY = "customizer-class"
        }
    }

    companion object {
        const val PREFIX = "http-interface-clients"
        private val NAME_PATTERN = Regex("^[a-z]+(-[a-z]+)*$")

        fun validateName(name: String) {
            require(NAME_PATTERN.matches(name)) {
                "Invalid http-interface client name '$name'. " +
                    "Expected lowercase words separated with '-', e.g. 'demo-client'."
            }
        }
    }
}
