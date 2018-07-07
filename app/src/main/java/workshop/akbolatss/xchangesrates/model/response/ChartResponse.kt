package workshop.akbolatss.xchangesrates.model.response

data class ChartResponse(
        var request: String,
        var code: Int,
        var data: ChartResponseData,
        var status: String
)