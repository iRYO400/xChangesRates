package workshop.akbolatss.xchangesrates.data.persistent

import androidx.room.Database
import androidx.room.RoomDatabase
import workshop.akbolatss.xchangesrates.data.persistent.dao.ChartDataDao
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.model.response.ChartInfo
import workshop.akbolatss.xchangesrates.model.response.ChartItem

@Database(
        entities = [(ChartData::class), (ChartInfo::class), (ChartItem::class)],
        version = 1,
        exportSchema = true
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun chartDataDao(): ChartDataDao

}
