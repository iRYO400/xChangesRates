package workshop.akbolatss.xchangesrates.data.persistent.model

import androidx.room.Embedded
import androidx.room.Entity
import workshop.akbolatss.xchangesrates.utils.extension.empty

@Entity(
    tableName = "chart",
    primaryKeys = ["exchange", "coin", "currency"]
)
data class Chart(
    val exchange: String,
    val coin: String,
    val currency: String,
    val source: String,
    @Embedded(prefix = "info_")
    val info: ChartInfo,
//    @Embedded(prefix = "unit_")
    val units: ArrayList<ChartUnit> //TODO mby divide
) {
    companion object {
        fun empty() = Chart(
            exchange = String.empty(),
            coin = String.empty(),
            currency = String.empty(),
            source = String.empty(),
            info = ChartInfo.empty(),
            units = arrayListOf()
        )
    }

    fun isEmpty() = this == empty()

}
