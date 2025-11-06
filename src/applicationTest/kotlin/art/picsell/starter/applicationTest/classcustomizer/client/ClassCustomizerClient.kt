package art.picsell.starter.applicationTest.classcustomizer.client

import art.picsell.starter.applicationTest.common.model.EchoRequest
import art.picsell.starter.applicationTest.common.model.EchoResponse
import art.picsell.starter.applicationTest.common.model.GreetingResponse
import art.picsell.starter.applicationTest.common.model.ItemResponse
import art.picsell.starter.client.annotation.HttpInterfaceClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

@HttpInterfaceClient(name = "class-customizer-client", path = "/api")
interface ClassCustomizerClient {

    @GetExchange("/greeting")
    fun greeting(@RequestParam name: String? = null): GreetingResponse

    @GetExchange("/items/{id}")
    fun getItem(@PathVariable id: Long): ItemResponse

    @PostExchange("/echo")
    fun echo(@RequestBody request: EchoRequest): EchoResponse
}
