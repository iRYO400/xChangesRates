package workshop.akbolatss.xchangesrates.domain.model

import workshop.akbolatss.xchangesrates.utils.extension.empty
import workshop.akbolatss.xchangesrates.utils.extension.emptyDate
import workshop.akbolatss.xchangesrates.utils.extension.localId
import java.math.BigDecimal
import java.util.*

data class Snapshot(
    val id: Long,
    val exchangerName: String,
    val coin: String,
    val currency: String,
    val updateTime: Date,
    val rate: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val change: BigDecimal,
    val change24: BigDecimal,
    val options: SnapshotOptions,
    val chartDots: List<ChartDot>
) {
    companion object {

        fun empty() = Snapshot(
            id = localId(),
            exchangerName = String.empty(),
            coin = String.empty(),
            currency = String.empty(),
            updateTime = emptyDate(),
            rate = BigDecimal.ZERO,
            high = BigDecimal.ZERO,
            low = BigDecimal.ZERO,
            change = BigDecimal.ZERO,
            change24 = BigDecimal.ZERO,
            options = SnapshotOptions.empty(),
            chartDots = emptyList()
        )
    }

    fun isEmpty(): Boolean =
        this == empty()
}
