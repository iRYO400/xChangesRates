package workshop.akbolatss.xchangesrates.data.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import workshop.akbolatss.xchangesrates.data.persistent.model.Exchange

@Dao
interface ExchangeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(exchanges: List<Exchange>): List<Long>

    @Query("SELECT * FROM exchange")
    suspend fun findAll(): List<Exchange>
}
