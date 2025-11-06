# Spring HTTP Interface Clients Starter

Small Spring Boot starter that automatically registers interfaces annotated with `@HttpInterfaceClient` as Spring 6 HTTP service proxies backed by `WebClient`. The starter keeps configuration in one place (`http-interface-clients.*`) and wires everything once you opt in via `@EnableHttpInterfaceClients`.

## Quick start

1. **Add the dependency**
   ```kotlin
   // build.gradle.kts
   dependencies {
       implementation("art.picsell.starter:spring-http-interface-clients-starter:0.0.1")
   }
   ```
2. **Enable client scanning** – place `@EnableHttpInterfaceClients` on a configuration class and optionally narrow scanning to specific packages.
   ```kotlin
   @Configuration
   @EnableHttpInterfaceClients("com.example.client")
   class HttpClientConfig
   ```
3. **Declare an HTTP interface** – annotate it with `@HttpInterfaceClient`, define request mappings with the standard `@GetExchange`, `@PostExchange`, etc., and inject it like any other Spring bean.
   ```kotlin
   @HttpInterfaceClient(name = "demo-client", path = "/api")
   interface DemoClient {

       @GetExchange("/greeting")
       fun greeting(@RequestParam name: String? = null): GreetingResponse

       @PostExchange("/echo")
       fun echo(@RequestBody request: EchoRequest): EchoResponse
   }
   ```
4. **Configure target URLs** – each client name must have a matching entry under the `http-interface-clients` prefix.
   ```yaml
   http-interface-clients:
     demo-client:
       url: https://example.com
       # optional WebClient customizer
       customizer-bean: demoClientCustomizer
       # or
       customizer-class: com.example.client.DemoClientCustomizer
   ```

After these steps, `DemoClient` is a Spring bean backed by `WebClient` and can be injected into your services without manual factory code.

## WebClient customization

Every client can opt into custom `WebClient` tweaks (headers, timeouts, codecs, etc.) without touching global configuration:

- `customizer-bean` – points to a named `WebClientCustomizer` bean that will be resolved from the application context.
- `customizer-class` – accepts a fully qualified class name (string) of a `WebClientCustomizer` bean that already exists in the Spring context; the starter simply looks it up by type.

Example showing both approaches:

```yaml
# application-class-customizer.yaml
http-interface-clients:
  class-customizer-client:
    url: https://example.com
    customizer-class: com.example.ClassHeaderCustomizer

# application-bean-customizer.yaml
http-interface-clients:
  bean-customizer-client:
    url: https://example.com
    customizer-bean: beanHeaderCustomizer
```

See [CONTRIBUTING.md](CONTRIBUTING.md) for contributor guidelines and publishing instructions.
