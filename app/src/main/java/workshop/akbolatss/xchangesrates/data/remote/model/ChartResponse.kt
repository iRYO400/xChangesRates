package workshop.akbolatss.xchangesrates.data.remote.model

import workshop.akbolatss.xchangesrates.model.response.ChartData

data class ChartResponse(
    val request: String,
    val code: Int,
    val data: ChartData, //TODO replace to DataResponse
    val status: String
)
