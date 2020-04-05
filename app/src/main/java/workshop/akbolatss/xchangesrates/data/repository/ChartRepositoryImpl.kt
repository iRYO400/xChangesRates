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
import workshop.akbolatss.xchangesrates.data.remote.service.ApiService
import workshop.akbolatss.xchangesrates.domain.repository.ChartRepository
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.data.remote.model.ChartResponse

class ChartRepositoryImpl(
        private val apiService: ApiService,
        private val chartDataDao: ChartDataDao
) : BaseRepository(), ChartRepository {

    override fun onAddChartData(chartData: ChartData): Single<Long> {
        return Single.fromCallable {
            chartDataDao.addChartData(chartData)
        }
    }

    fun onGetById(id: Long): Single<ChartData> {
        return chartDataDao.getById(id)
    }

    override fun onDeleteChartData(chartData: ChartData): Completable {
        return Completable.fromAction {
            chartDataDao.deleteChartData(chartData)
        }
    }

    override fun onDeleteChartData(id: Long): Completable {
        return Completable.fromAction {
            chartDataDao.deleteChartData(id)
        }
    }

    override fun onUpdateChartData(chartData: ChartData): Observable<ChartData> {
        return Observable.fromCallable {
            chartDataDao.updateChartData(chartData)
            chartData
        }
    }

    override fun getQueryResult(coin: String, exchange: String, currency: String, term: String): Observable<ChartResponse> {
        return apiService.getCurrency(coin, exchange, currency, term)
    }

    override fun getSnapshot(coin: String, exchange: String, currency: String, term: String): Observable<ChartResponse> {
        return apiService.getSnapshot(coin, exchange, currency, term)
    }

    override suspend fun findBy(itemId: Long): ChartData =
            chartDataDao.findBy(itemId)

    override suspend fun findList(): List<ChartData> = chartDataDao.findList()

    override suspend fun updateChartData(chart: ChartData): Either<Failure, None> {
        return apiCall(call = {
            apiService.getChart(chart.coin, chart.exchange, chart.currency, chart.timingName)
        }, mapResponse = { response ->
            response.data.copy(id = chart.id,
                    coin = chart.coin,
                    timingName = chart.timingName,
                    timingIndex = chart.timingIndex,
                    options = chart.options,
                    isNotificationEnabled = chart.isNotificationEnabled
            )
        }).flatMap { newChart ->
            saveResponse(newChart)
        }
    }

    private suspend fun saveResponse(chart: ChartData): Either<Failure, None> {
        return update(map = {
        }, query = {
            chartDataDao.update(chart)
        })
    }

}
