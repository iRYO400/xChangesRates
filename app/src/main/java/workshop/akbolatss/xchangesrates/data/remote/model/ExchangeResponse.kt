package workshop.akbolatss.xchangesrates.data.remote.model


data class ExchangeResponse(
    val ident: String,
    val caption: String,
    val currencies: Map<String, List<String>>
)
