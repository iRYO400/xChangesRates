package workshop.akbolatss.xchangesrates.data.remote.model

import java.math.BigDecimal

data class InfoResponse(
    val buy: BigDecimal,
    val sell: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val volume: BigDecimal,
    val last: BigDecimal,
    val updated: String,
    val timestamp: Long,
    val change: BigDecimal,
    val change24: BigDecimal,
    val started: String
)

