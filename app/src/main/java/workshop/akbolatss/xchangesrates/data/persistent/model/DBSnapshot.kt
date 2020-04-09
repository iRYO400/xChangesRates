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
data class DBSnapshot(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val exchangerName: String,
    val coin: String,
    val currency: String,
    val source: String,
    val updateTime: Date,
    val rate: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal
)
