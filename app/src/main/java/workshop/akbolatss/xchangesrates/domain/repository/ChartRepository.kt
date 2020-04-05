package workshop.akbolatss.xchangesrates.domain.repository

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.model.response.ChartResponse

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

interface ChartRepository {

    fun getQueryResult(coin: String, exchange: String,
                       currency: String, term: String): Observable<ChartResponse>

    fun getSnapshot(coin: String, exchange: String,
                    currency: String, term: String): Observable<ChartResponse>

    val allChartData: Flowable<List<ChartData>>

    fun onAddChartData(chartData: ChartData): Single<Long>

    fun onDeleteChartData(chartData: ChartData): Completable

    fun onUpdateChartData(chartData: ChartData): Observable<ChartData>
    fun onDeleteChartData(id: Long): Completable
}
