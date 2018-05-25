package workshop.akbolatss.xchangesrates.repositories

import io.reactivex.Single
import workshop.akbolatss.xchangesrates.model.dao.Snapshot
import workshop.akbolatss.xchangesrates.model.dao.SnapshotChart
import workshop.akbolatss.xchangesrates.model.dao.SnapshotInfo
import workshop.akbolatss.xchangesrates.model.response.ChartResponse

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

interface ChartRepository {

    val allChartData: Single<List<Snapshot>>

    val activeChartData: Single<List<Snapshot>>

    fun getQueryResult(coin: String, exchange: String,
                       currency: String, term: String): Single<ChartResponse>

    fun getChartDataInfo(key: Long): Single<SnapshotInfo>

    fun getSnapshot(coin: String, exchange: String,
                    currency: String, term: String): Single<ChartResponse>

    fun onAddChartData(data: Snapshot, dataInfo: SnapshotInfo, dataCharts: List<SnapshotChart>): Single<Boolean>

    fun onDeleteChartData(key: Long): Single<Int>

    fun onUpdateChartData(snapshot: Snapshot, dataInfo: SnapshotInfo, chartsList: List<SnapshotChart>)

    fun onOptionsChanged(key: Long, isActive: Boolean, timing: String): Single<Int>
}
