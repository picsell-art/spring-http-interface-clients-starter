package art.picsell.starter.applicationTest.demo.client

import art.picsell.starter.client.annotation.HttpInterfaceClient
import art.picsell.starter.applicationTest.demo.model.EchoRequest
import art.picsell.starter.applicationTest.demo.model.EchoResponse
import art.picsell.starter.applicationTest.demo.model.GreetingResponse
import art.picsell.starter.applicationTest.demo.model.ItemResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

@HttpInterfaceClient(name = "demo-client", path = "/api")
interface DemoClient {

    @GetExchange("/greeting")
    fun greeting(@RequestParam name: String? = null): GreetingResponse

    @GetExchange("/items/{id}")
    fun getItem(@PathVariable id: Long): ItemResponse

    @PostExchange("/echo")
    fun echo(@RequestBody request: EchoRequest): EchoResponse
}
