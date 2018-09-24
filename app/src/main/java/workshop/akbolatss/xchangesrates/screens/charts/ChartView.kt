package workshop.akbolatss.xchangesrates.screens.charts

import workshop.akbolatss.xchangesrates.base.BaseView
import workshop.akbolatss.xchangesrates.model.response.ChartData

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

interface ChartView : BaseView {

    fun onLoadLineChart(chartData: ChartData)
    fun toast(message: String)
    fun onSaveSnapshot(isSuccess: Boolean, chartData: ChartData)
}
