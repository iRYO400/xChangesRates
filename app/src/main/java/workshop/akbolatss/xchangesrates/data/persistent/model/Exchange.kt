package workshop.akbolatss.xchangesrates.data.persistent.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "exchange")
data class Exchange(
    @PrimaryKey
    val id: String,
    val caption: String,
    val currencies: Map<String, List<String>>,
    val updateTime: Date
)
