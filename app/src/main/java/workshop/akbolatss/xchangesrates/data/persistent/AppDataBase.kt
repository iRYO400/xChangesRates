package workshop.akbolatss.xchangesrates.data.persistent

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import workshop.akbolatss.xchangesrates.data.persistent.converter.RoomConverters
import workshop.akbolatss.xchangesrates.data.persistent.dao.ExchangeDao
import workshop.akbolatss.xchangesrates.data.persistent.dao.SnapshotDao
import workshop.akbolatss.xchangesrates.data.persistent.model.ExchangeEntity
import workshop.akbolatss.xchangesrates.data.persistent.model.SnapshotEntity

@Database(
    entities = [
        ExchangeEntity::class,
        SnapshotEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun exchangeDao(): ExchangeDao

    abstract fun snapshotDao(): SnapshotDao
}
