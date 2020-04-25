package workshop.akbolatss.xchangesrates.domain.model

import java.math.BigDecimal

data class Chart(
    val rate: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val change: BigDecimal,
    val change24: BigDecimal,
    val chartDots: List<ChartDot>
) {

    companion object {
        fun empty() = Chart(
            rate = BigDecimal.ZERO,
            high = BigDecimal.ZERO,
            low = BigDecimal.ZERO,
            change = BigDecimal.ZERO,
            change24 = BigDecimal.ZERO,
            chartDots = emptyList()
        )
    }

}
