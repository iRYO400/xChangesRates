package workshop.akbolatss.xchangesrates.data.persistent.model

import java.math.BigDecimal

data class ChartUnit(
    val timestamp: Long,
    val price: BigDecimal,
    val market: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal
)
