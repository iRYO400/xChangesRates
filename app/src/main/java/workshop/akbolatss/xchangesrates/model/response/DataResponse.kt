package workshop.akbolatss.xchangesrates.model.response

data class DataResponse(
    val exchange: String,
    val currency: String,
    val source: String,
    val info: InfoResponse,
    val chart: List<ChartItemResponse>
)
