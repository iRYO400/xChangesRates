package workshop.akbolatss.xchangesrates.screens.charts

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import workshop.akbolatss.xchangesrates.base.BasePresenter
import workshop.akbolatss.xchangesrates.model.ExchangeModel
import workshop.akbolatss.xchangesrates.model.mapper.ChartDataMapper
import workshop.akbolatss.xchangesrates.model.response.ChartResponse
import workshop.akbolatss.xchangesrates.model.response.ChartResponseData
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository

class ChartPresenter(private val mRepository: DBChartRepository) : BasePresenter<ChartView>() {

    /**
     * Main model
     */
    var exchangeModel: ExchangeModel? = null

    /**
     * Response от сервера
     */
    var mChartData: ChartResponseData? = null

    var mCoinCode: String? = null
    var mCurrencyCode: String? = null
    var mTerm: String? = null

    private var compositeDisposable: CompositeDisposable? = null

    override fun onViewAttached(view: ChartView?) {
        super.onViewAttached(view)
        compositeDisposable = CompositeDisposable()
    }

    override fun onViewDetached(view: ChartView?) {
        super.onViewDetached(view)
        if (compositeDisposable != null) {
            compositeDisposable!!.clear()
        }
    }

    fun onUpdate() {
        view.onShowLoading()
        compositeDisposable!!.add(
                mRepository.getQueryResult(mCoinCode,
                        exchangeModel!!.ident,
                        mCurrencyCode,
                        mTerm)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            mChartData = response.data
                            mChartData!!.coin = mCoinCode
                            view.onLoadLineChart(mChartData!!)
                            view.onHideLoading()
                        }, {
                            view.onHideLoading()
                        }))
    }

    fun onSaveSnap() {
        view.onShowLoading()
        val mapper = ChartDataMapper(mChartData, mChartData!!.info, mChartData!!.chart)
        //        Flowable<Object> s = Single.concat(mapper.transformD(mChartData), //TODO Улучши, когда твои скиллы по RxJava 2 поднимутся
        //                mapper.transformInfoD(mChartData.getInfo()),
        //                mapper.transformChartsD(mChartData.getChart()));
        compositeDisposable!!.add(
                mRepository.onAddChartData(mapper.data, mapper.dataInfo, mapper.chartsList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            view.onHideLoading()
                        }, {
                            view.onHideLoading()
                        })
        )
    }
}
