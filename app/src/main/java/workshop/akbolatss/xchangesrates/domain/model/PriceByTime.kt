package workshop.akbolatss.xchangesrates.domain.model

import java.math.BigDecimal

data class PriceByTime(
    val timestamp: Long,
    val price: BigDecimal
)
