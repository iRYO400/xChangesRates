package workshop.akbolatss.xchangesrates.data.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import workshop.akbolatss.xchangesrates.data.persistent.model.ExchangeEntity

@Dao
interface ExchangeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(exchanges: List<ExchangeEntity>): List<Long>

    @Query("SELECT * FROM exchange")
    suspend fun findAll(): List<ExchangeEntity>
}
