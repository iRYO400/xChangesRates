package workshop.akbolatss.xchangesrates.model.mapper

import java.util.ArrayList
import java.util.concurrent.Callable

import io.reactivex.Single
import workshop.akbolatss.xchangesrates.model.dao.Snapshot
import workshop.akbolatss.xchangesrates.model.dao.SnapshotChart
import workshop.akbolatss.xchangesrates.model.dao.SnapshotInfo
import workshop.akbolatss.xchangesrates.model.response.ChartResponseData
import workshop.akbolatss.xchangesrates.model.response.ChartResponseDataChart
import workshop.akbolatss.xchangesrates.model.response.ChartResponseDataInfo
import workshop.akbolatss.xchangesrates.utils.Constants

import workshop.akbolatss.xchangesrates.utils.Constants.HOUR_24

/**
 * Author: Akbolat Sadvakassov
 * Date: 14.01.2018
 */

class ChartDataMapper(data: ChartResponseData, dataInfo: ChartResponseDataInfo, chartsList: List<ChartResponseDataChart>) {

    val data: Snapshot?
    val dataInfo: SnapshotInfo?
    val chartsList: List<SnapshotChart>?

    init {
        this.data = transformData(data)
        this.dataInfo = transformInfo(dataInfo)
        this.chartsList = transformCharts(chartsList)
    }

    fun transformData(responseData: ChartResponseData): Snapshot {
        val snapshot = Snapshot()
        snapshot.coin = responseData.coin
        snapshot.currency = responseData.currency
        snapshot.exchange = responseData.exchange
        snapshot.source = responseData.source
        snapshot.isActiveForGlobal = false
        snapshot.timing = Constants.HOUR_24
        snapshot.isIntervalEnabled = false
        snapshot.intervalNumber = 10
        snapshot.intervalType = 2
        snapshot.isNotifyPersistent = false
        return snapshot
    }

    fun transformInfo(dataInfo: ChartResponseDataInfo): SnapshotInfo {
        val info = SnapshotInfo()
        info.buy = dataInfo.buy
        info.change = dataInfo.change
        info.change24 = dataInfo.change24
        info.high = dataInfo.high
        info.last = dataInfo.last
        info.low = dataInfo.low
        info.multiply = dataInfo.multiply
        info.sell = dataInfo.sell
        info.started = dataInfo.started
        info.timestamp = dataInfo.timestamp
        info.updated = dataInfo.updated
        info.volume = dataInfo.volume
        return info
    }

    fun transformCharts(list: List<ChartResponseDataChart>): List<SnapshotChart> {
        val chartsList: MutableList<SnapshotChart>
        chartsList = ArrayList()
        for (i in list.indices) {
            val dataCharts = SnapshotChart()
            dataCharts.high = list[i].high
            dataCharts.low = list[i].low
            dataCharts.market = list[i].market
            dataCharts.price = list[i].price
            dataCharts.timestamp = list[i].timestamp
            chartsList.add(dataCharts)
        }
        return chartsList
    }
}
