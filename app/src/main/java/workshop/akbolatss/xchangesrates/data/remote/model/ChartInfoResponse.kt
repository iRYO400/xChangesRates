package workshop.akbolatss.xchangesrates.data.remote.model

import workshop.akbolatss.xchangesrates.data.persistent.model.ChartInfo
import java.math.BigDecimal

data class ChartInfoResponse(
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

object ChartInfoMap {

    fun ChartInfoResponse.toEntity() = ChartInfo(
        buy = buy,
        sell = sell,
        high = high,
        low = low,
        volume = volume,
        last = last,
        updated = updated,
        timestamp = timestamp,
        change = change,
        change24 = change24,
        started = started
    )
}
