package workshop.akbolatss.xchangesrates.room

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
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