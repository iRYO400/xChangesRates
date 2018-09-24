package workshop.akbolatss.xchangesrates.model.response

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import workshop.akbolatss.xchangesrates.room.RoomConvertors


@Entity
@TypeConverters(RoomConvertors::class)
data class ChartData(
        @PrimaryKey(autoGenerate = true)
        var id: Long,
        var exchange: String,
        var currency: String,
        var source: String,
        var coin: String, // Custom, BTC, ETH...
        var timingName: String = "24h", // Custom, e.g. 10min, 24hours, week, year
        var timingIndex: Int = 4, // Check strings.xml array_history_timing, 4 is "24h"
        @Embedded(prefix = "info")
        var info: ChartInfo,
        var chart: ArrayList<ChartItem>, // Data for LineChart
        var isNotificationEnabled: Boolean, // Custom, for Stick notification
        @Embedded(prefix = "options")
        var options: ChartOptions = ChartOptions()
)

@Entity
data class ChartOptions(
        @PrimaryKey(autoGenerate = true)
        var optionsId: Long = 0,
        var isSmartEnabled: Boolean = true,
        var isStickedEnabled: Boolean = false, // Sticked notification in status bar
        var intervalUpdateIndex: Int = 13 // Custom, Interval update index, @see strings.xml array_interval_updates. 1 is 1 minute
)

@Entity
data class ChartInfo(
        @PrimaryKey(autoGenerate = true)
        var infoId: Long,
        val volume: Float?,
        val high: String?,
        val change24: Float?,
        val last: String?,
        val low: String?,
        val buy: String?,
        val sell: String?,
        val change: Float?,
        val started: String?,
        val multiply: String?,
        val updated: String?,
        val timestamp: Long?
)

@Entity
class ChartItem(
        @PrimaryKey(autoGenerate = true)
        var chartId: Long,
        val market: Float?,
        val high: String?,
        val low: String?,
        val price: String?,
        val timestamp: Long?
)