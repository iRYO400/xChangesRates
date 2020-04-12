package workshop.akbolatss.xchangesrates.data.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import workshop.akbolatss.xchangesrates.data.persistent.model.SnapshotEntity

@Dao
interface SnapshotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(snapshot: SnapshotEntity): Long

    @Query("SELECT * FROM snapshot WHERE exchangerName = :exchange AND coin = :coin AND currency = :currency")
    suspend fun findBy(exchange: String, coin: String, currency: String): SnapshotEntity?

    @Query("SELECT * FROM snapshot")
    fun findAll(): Flow<List<SnapshotEntity>>
}
