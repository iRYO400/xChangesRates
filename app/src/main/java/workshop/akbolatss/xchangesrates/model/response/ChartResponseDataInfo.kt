package workshop.akbolatss.xchangesrates.model.response

data class ChartResponseDataInfo(
    var volume: Float,
    var high: String,
    var change24: Float,
    var last: String,
    var low: String,
    var buy: String,
    var sell: String,
    var change: Float,
    var started: String,
    var multiply: String,
    var updated: String,
    var timestamp: Long
)