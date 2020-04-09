package workshop.akbolatss.xchangesrates.data.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import workshop.akbolatss.xchangesrates.data.persistent.model.DBSnapshot

@Dao
interface SnapshotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(snapshot: DBSnapshot): Long

    @Query("SELECT * FROM snapshot WHERE exchangerName = :exchange AND coin = :coin AND currency = :currency")
    suspend fun findBy(exchange: String, coin: String, currency: String): DBSnapshot?
}
