package workshop.akbolatss.xchangesrates.screens.snapshots

import workshop.akbolatss.xchangesrates.base.BaseView
import workshop.akbolatss.xchangesrates.base.LoadingView
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.model.response.ChartInfo

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

interface SnapshotsView : BaseView, LoadingView {

    /**
     * Loading all snapshots to RecyclerView
     */
    fun loadChartDatas(chartDataList: List<ChartData>)

    /**
     * Loading single snapshot to RV
     */
    fun loadChartData(chartData: ChartData, pos: Int)


    fun onErrorChartItem(pos: Int)
    fun startService()
    fun stopService()
    fun toast(s: String)
    fun enqueueWorker(it: ChartData)
    fun dequeueWorker(chartData: ChartData)
}
