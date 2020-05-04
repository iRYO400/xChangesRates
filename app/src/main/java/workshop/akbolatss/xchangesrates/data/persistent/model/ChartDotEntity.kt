package workshop.akbolatss.xchangesrates.data.persistent.model

import java.math.BigDecimal

data class ChartDotEntity(
    val timestamp: Long,
    val price: BigDecimal
)
