package workshop.akbolatss.xchangesrates.domain.model

import java.math.BigDecimal

data class ChartDot(
    val timestamp: Long,
    val price: BigDecimal
)
