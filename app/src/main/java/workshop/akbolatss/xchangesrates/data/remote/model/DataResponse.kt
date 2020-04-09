package workshop.akbolatss.xchangesrates.data.remote.model

data class DataResponse(
    val exchange: String,
    val currency: String,
    val source: String,
    val info: ChartInfoResponse,
    val chart: List<ChartUnitResponse>
)
