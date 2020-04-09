package workshop.akbolatss.xchangesrates.data.remote.model

data class ChartResponse(
    val request: String,
    val code: Int,
    val data: DataResponse,
    val status: String
)
