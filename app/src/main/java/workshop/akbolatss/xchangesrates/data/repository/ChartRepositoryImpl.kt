package workshop.akbolatss.xchangesrates.data.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import workshop.akbolatss.xchangesrates.base.BaseRepository
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.base.resource.flatMap
import workshop.akbolatss.xchangesrates.data.persistent.dao.ChartDataDao
import workshop.akbolatss.xchangesrates.data.persistent.model.Chart
import workshop.akbolatss.xchangesrates.data.remote.model.ChartInfoMap.toEntity
import workshop.akbolatss.xchangesrates.data.remote.model.ChartResponse
import workshop.akbolatss.xchangesrates.data.remote.model.ChartUnitMap.toEntityList
import workshop.akbolatss.xchangesrates.data.remote.model.DataResponse
import workshop.akbolatss.xchangesrates.data.remote.service.ApiService
import workshop.akbolatss.xchangesrates.domain.repository.ChartRepository
import workshop.akbolatss.xchangesrates.model.response.ChartData

class ChartRepositoryImpl(
    private val apiService: ApiService,
    private val chartDao: ChartDataDao
) : BaseRepository(), ChartRepository {

    override suspend fun loadChart(
        exchange: String,
        coin: String,
        currency: String,
        timing: String
    ): Either<Failure, None> {
        return apiCall(call = {
            apiService.getChart(exchange, coin, currency, timing)
        }, mapResponse = {
            it.data
        }).flatMap { response ->
            saveChart(coin, response)
        }
    }

    private suspend fun saveChart(coin: String, response: DataResponse): Either<Failure, None> {
        return insert(map = {
            Chart(
                exchange = response.exchange,
                coin = coin,
                currency = response.currency,
                source = response.source,
                info = response.info.toEntity(),
                units = response.chart.toEntityList()
            )
        }, query = { chart ->
            chartDao.insert(chart)
        })
    }

    override suspend fun findBy(exchange: String, coin: String, currency: String): Chart =
        chartDao.findBy(exchange, coin, currency) ?: Chart.empty()

    override suspend fun findBy(itemId: Long): ChartData =
        chartDao.findBy(itemId)

    override suspend fun findList(): List<ChartData> = chartDao.findList()

    override suspend fun updateChartData(chart: ChartData): Either<Failure, None> {
        return apiCall(call = {
            apiService.getChart(chart.coin, chart.exchange, chart.currency, chart.timingName)
        }, mapResponse = { response ->
            response.data.copy(
//                id = chart.id,
//                coin = chart.coin,
//                timingName = chart.timingName,
//                timingIndex = chart.timingIndex,
//                options = chart.options,
//                isNotificationEnabled = chart.isNotificationEnabled
            )
        }).flatMap { newChart ->
            Either.Right(None())
        }
    }

}
