package workshop.akbolatss.xchangesrates.data.remote.model

data class StatsResponse(
    val request: String,
    val data: List<ExchangeResponse>
)
