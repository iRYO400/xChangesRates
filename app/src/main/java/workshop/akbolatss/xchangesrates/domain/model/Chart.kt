package workshop.akbolatss.xchangesrates.domain.model

import java.math.BigDecimal

data class Chart(
    val rate: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val units: List<PriceByTime>
) {

    companion object {
        fun empty() = Chart(
            rate = BigDecimal.ZERO,
            high = BigDecimal.ZERO,
            low = BigDecimal.ZERO,
            units = emptyList()
        )
    }

}
