package workshop.akbolatss.xchangesrates.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Flowable
import io.reactivex.Single
import workshop.akbolatss.xchangesrates.model.response.ChartData

@Dao
interface ChartDataDao {

    @get:Query("SELECT * FROM ChartData")
    val all: Flowable<List<ChartData>>

    @get:Query("SELECT * FROM ChartData")
    val allObservable: Single<List<ChartData>>

    @get:Query("SELECT * FROM ChartData WHERE isNotificationEnabled = 1")
    val activeOnly: Single<List<ChartData>>

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
