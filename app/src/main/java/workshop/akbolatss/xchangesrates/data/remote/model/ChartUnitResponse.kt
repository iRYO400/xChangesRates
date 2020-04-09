package workshop.akbolatss.xchangesrates.data.remote.model

import workshop.akbolatss.xchangesrates.data.persistent.model.ChartUnit
import java.math.BigDecimal

data class ChartUnitResponse(
    val timestamp: Long,
    val price: BigDecimal,
    val market: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal
)

object ChartUnitMap {

    fun ChartUnitResponse.toEntity() =
        ChartUnit(
            timestamp = timestamp,
            price = price,
            market = market,
            high = high,
            low = low
        )

    fun List<ChartUnitResponse>.toEntityList() =
        this.mapTo(arrayListOf()) { it.toEntity() }
}
