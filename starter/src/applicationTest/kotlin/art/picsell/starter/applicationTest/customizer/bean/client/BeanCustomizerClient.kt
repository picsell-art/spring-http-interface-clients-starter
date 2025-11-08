package art.picsell.starter.applicationTest.customizer.bean.client

import art.picsell.starter.applicationTest.testutils.GreetingResponse
import art.picsell.starter.client.annotation.HttpInterfaceClient
import org.springframework.web.service.annotation.GetExchange

@HttpInterfaceClient(name = "bean-customizer-client", path = "/api")
interface BeanCustomizerClient {

    @GetExchange("/bean")
    fun beanGreeting(): GreetingResponse
}
