package workshop.akbolatss.xchangesrates.screens.snapshots

import workshop.akbolatss.xchangesrates.base.BaseView
import workshop.akbolatss.xchangesrates.model.response.ChartData

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

interface SnapshotsView : BaseView {

    /**
     * Loading all snapshots to RecyclerView
     */
    fun loadChartDatas(chartDataList: List<ChartData>)

    /**
     * Loading single snapshot to RV
     */
    fun loadChartData(chartData: ChartData, pos: Int)


    fun onErrorChartItem(pos: Int)
    fun enqueueWorker(it: ChartData)
    fun dequeueWorker(chartData: ChartData)
}
