package workshop.akbolatss.xchangesrates.model.response

import java.math.BigDecimal

data class ChartItemResponse(
    val timestamp: Long,
    val price: BigDecimal,
    val market: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal
)
