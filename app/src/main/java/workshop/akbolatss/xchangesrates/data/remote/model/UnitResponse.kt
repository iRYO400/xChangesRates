package workshop.akbolatss.xchangesrates.data.remote.model

import java.math.BigDecimal

data class UnitResponse(
    val timestamp: Long,
    val price: BigDecimal,
    val market: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal
)
