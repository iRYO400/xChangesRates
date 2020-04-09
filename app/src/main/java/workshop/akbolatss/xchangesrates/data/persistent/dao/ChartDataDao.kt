package workshop.akbolatss.xchangesrates.data.persistent.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Single
import workshop.akbolatss.xchangesrates.data.persistent.model.Chart
import workshop.akbolatss.xchangesrates.model.response.ChartData

@Dao
interface ChartDataDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(chart: Chart): Long

    @Query("SELECT * FROM ChartData WHERE id = :id")
    suspend fun findBy(id: Long): ChartData

    @Query("SELECT * FROM ChartData")
    suspend fun findList(): List<ChartData>

    @Query("SELECT * FROM chart WHERE exchange = :exchange AND coin = :coin AND currency = :currency")
    suspend fun findBy(exchange: String, coin: String, currency: String): Chart?

    @Update
    suspend fun update(chartData: ChartData): Int

    @Update
    fun updateChartData(chartData: ChartData)
}
