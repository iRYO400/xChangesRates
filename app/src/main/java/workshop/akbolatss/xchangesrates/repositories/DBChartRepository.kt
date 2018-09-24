package workshop.akbolatss.xchangesrates.repositories

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.model.response.ChartResponse
import workshop.akbolatss.xchangesrates.networking.APIService
import workshop.akbolatss.xchangesrates.room.ChartDataDao


class DBChartRepository(private val mApiService: APIService, private val mChartDataDao: ChartDataDao) : ChartRepository {

    override fun onAddChartData(chartData: ChartData): Single<Long> {
        return Single.fromCallable {
            mChartDataDao.addChartData(chartData)
        }
    }

    override val allChartData: Flowable<List<ChartData>>
        get() = mChartDataDao.all

    val allObservable: Single<List<ChartData>>
        get() = mChartDataDao.allObservable

    val allActive: Single<List<ChartData>>
        get() = mChartDataDao.activeOnly

    fun onGetById(id: Long): Single<ChartData> {
        return mChartDataDao.getById(id)
    }

    override fun onDeleteChartData(chartData: ChartData): Completable {
        return Completable.fromAction {
            mChartDataDao.deleteChartData(chartData)
        }
    }

    override fun onDeleteChartData(id: Long): Completable {
        return Completable.fromAction {
            mChartDataDao.deleteChartData(id)
        }
    }

    override fun onUpdateChartData(chartData: ChartData): Observable<ChartData> {
        return Observable.fromCallable {
            mChartDataDao.updateChartData(chartData)
            chartData
        }
    }

    override fun getQueryResult(coin: String, exchange: String, currency: String, term: String): Observable<ChartResponse> {
        return mApiService.getCurrency(coin, exchange, currency, term)
    }

    override fun getSnapshot(coin: String, exchange: String, currency: String, term: String): Observable<ChartResponse> {
        return mApiService.getSnapshot(coin, exchange, currency, term)
    }
}
