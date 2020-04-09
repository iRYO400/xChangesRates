package workshop.akbolatss.xchangesrates.data.persistent

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import workshop.akbolatss.xchangesrates.data.persistent.converter.RoomConverters
import workshop.akbolatss.xchangesrates.data.persistent.dao.ChartDataDao
import workshop.akbolatss.xchangesrates.data.persistent.dao.ExchangeDao
import workshop.akbolatss.xchangesrates.data.persistent.model.Chart
import workshop.akbolatss.xchangesrates.data.persistent.model.Exchange
import workshop.akbolatss.xchangesrates.model.response.ChartData

@Database(
    entities = [
        Exchange::class,
        Chart::class,
        ChartData::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun exchangeDao(): ExchangeDao

    abstract fun chartDataDao(): ChartDataDao

}
