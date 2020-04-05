package workshop.akbolatss.xchangesrates.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.model.response.ChartInfo
import workshop.akbolatss.xchangesrates.model.response.ChartItem

@Database(entities = [(ChartData::class), (ChartInfo::class), (ChartItem::class)], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun chartDataDao(): ChartDataDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ImageResponse ADD Column field1 INTEGER DEFAULT 0 NOT NULL")
            }

        }
    }
}
