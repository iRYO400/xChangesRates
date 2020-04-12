package workshop.akbolatss.xchangesrates.data.persistent.model

import java.math.BigDecimal

data class PriceByTimeEntity(
    val timestamp: Long,
    val price: BigDecimal
)
