package workshop.akbolatss.xchangesrates.data.persistent

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import workshop.akbolatss.xchangesrates.data.persistent.converter.RoomConverters
import workshop.akbolatss.xchangesrates.data.persistent.dao.ChartDataDao
import workshop.akbolatss.xchangesrates.data.persistent.model.Exchange
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.model.response.ChartInfo
import workshop.akbolatss.xchangesrates.model.response.ChartItem

@Database(
    entities = [Exchange::class,
        (ChartData::class), (ChartInfo::class), (ChartItem::class)],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun chartDataDao(): ChartDataDao

}
