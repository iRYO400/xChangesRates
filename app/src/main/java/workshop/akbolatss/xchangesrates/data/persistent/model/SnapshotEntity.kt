package workshop.akbolatss.xchangesrates.data.persistent.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.*

@Entity(
    tableName = "snapshot",
    indices = [Index("exchangerName", "coin", "currency")]
)
data class SnapshotEntity(
    @PrimaryKey(autoGenerate = true)
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
    val charts: List<PriceByTimeEntity>
)
