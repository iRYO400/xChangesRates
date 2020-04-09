package workshop.akbolatss.xchangesrates.domain.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.data.persistent.model.Chart
import workshop.akbolatss.xchangesrates.data.remote.model.ChartResponse
import workshop.akbolatss.xchangesrates.model.response.ChartData

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

interface ChartRepository {

    suspend fun findBy(itemId: Long): ChartData

    suspend fun findList(): List<ChartData>

    suspend fun updateChartData(chart: ChartData): Either<Failure, None>

    suspend fun loadChart(
        exchange: String,
        coin: String,
        currency: String,
        timing: String
    ): Either<Failure, None>

    suspend fun findBy(
        exchange: String,
        coin: String,
        currency: String
    ): Chart

    fun getQueryResult(
        coin: String, exchange: String,
        currency: String, term: String
    ): Observable<ChartResponse>

    fun getSnapshot(
        coin: String, exchange: String,
        currency: String, term: String
    ): Observable<ChartResponse>

    fun onAddChartData(chartData: ChartData): Single<Long>
    fun onDeleteChartData(chartData: ChartData): Completable
    fun onUpdateChartData(chartData: ChartData): Observable<ChartData>
    fun onDeleteChartData(id: Long): Completable
}
