package workshop.akbolatss.xchangesrates.data.persistent.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Single
import workshop.akbolatss.xchangesrates.data.persistent.model.Exchange
import workshop.akbolatss.xchangesrates.model.response.ChartData

@Dao
interface ChartDataDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertList(exchanges: List<Exchange>): List<Long>

    @Query("SELECT * FROM ChartData WHERE id = :id")
    suspend fun findBy(id: Long): ChartData

    @Query("SELECT * FROM ChartData")
    suspend fun findList(): List<ChartData>

    @Update
    suspend fun update(chartData: ChartData): Int

    @Query("SELECT * FROM ChartData WHERE id = :id")
    fun getById(id: Long): Single<ChartData>

    @Insert(onConflict = REPLACE)
    fun addChartData(chartData: ChartData): Long

    @Delete
    fun deleteChartData(chartData: ChartData)

    @Query("DELETE FROM ChartData WHERE id = :id")
    fun deleteChartData(id: Long)

    @Update
    fun updateChartData(chartData: ChartData)
}
