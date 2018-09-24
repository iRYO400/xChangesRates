package workshop.akbolatss.xchangesrates.model.response

data class ChartResponse(
        var request: String,
        var code: Int,
        var data: ChartData,
        var status: String
)