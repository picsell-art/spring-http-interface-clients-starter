package art.picsell.starter.applicationTest.testutils

data class GreetingResponse(
    val message: String
)

data class ItemResponse(
    val id: Long,
    val title: String
)

data class EchoRequest(
    val value: String
)

data class EchoResponse(
    val value: String,
    val extra: String
)
