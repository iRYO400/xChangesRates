package workshop.akbolatss.xchangesrates.data.persistent.model

import workshop.akbolatss.xchangesrates.utils.extension.defaultVal
import workshop.akbolatss.xchangesrates.utils.extension.empty
import java.math.BigDecimal

data class ChartInfo(
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
) {
    companion object {
        fun empty() = ChartInfo(
            buy = BigDecimal.ZERO,
            sell = BigDecimal.ZERO,
            high = BigDecimal.ZERO,
            low = BigDecimal.ZERO,
            volume = BigDecimal.ZERO,
            last = BigDecimal.ZERO,
            updated = String.empty(),
            timestamp = defaultVal(),
            change = BigDecimal.ZERO,
            change24 = BigDecimal.ZERO,
            started = String.empty()
        )
    }
}
