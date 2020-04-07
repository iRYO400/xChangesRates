package workshop.akbolatss.xchangesrates.presentation.chart

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import workshop.akbolatss.xchangesrates.base.BasePresenter
import workshop.akbolatss.xchangesrates.data.remote.model.ExchangeResponse
import workshop.akbolatss.xchangesrates.data.repository.ChartRepositoryImpl
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.model.response.ChartOptions

class ChartPresenter(private val mRepository: ChartRepositoryImpl) : BasePresenter<ChartView>() {

    /**
     * Main model
     */
    lateinit var exchangeModel: ExchangeResponse
    var exchangeModelPos: Int = 0

    /**
     * Chart Model
     */
    lateinit var mChartData: ChartData

    /**
     * Timing, e.g. 10min, 1h, 3h, 12h, 1 day, etc.
     */
    lateinit var mTerm: String

    /**
     * ID of crypto valency, btc, eth...
     */
    lateinit var mCoinCode: String
    var coinCodePos: Int = 0

    /**
     * ID of standard valency, KZT, USD...
     */
    lateinit var mCurrencyCode: String
    var currencyCodePos: Int = 0

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
//        view.onShowLoading()
        compositeDisposable!!.add(
            mRepository.getQueryResult(
                mCoinCode,
                exchangeModel.ident,
                mCurrencyCode,
                mTerm
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    mChartData = response.data
                    mChartData.coin = mCoinCode
                    view.onLoadLineChart(mChartData)
//                            view.onHideLoading()
                }, {
                    //                            view.onHideLoading()
                })
        )
    }

    fun saveChartData() {
        mChartData.coin = mCoinCode
        mChartData.currency = mCurrencyCode
        mChartData.timingName = "12h"
        mChartData.timingIndex = 3 // 12 hours
        mChartData.options = ChartOptions()
        mChartData.options.isSmartEnabled = true
        mChartData.isNotificationEnabled = false
        compositeDisposable!!.add(
            mRepository.onAddChartData(mChartData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    view.toast("Successfully saved!")
                    mChartData.id = it
                    view.onSaveSnapshot(true, mChartData)
                }, {
                    view.onSaveSnapshot(false, mChartData)
                    it.printStackTrace()
                    Log.e("TAG", "Error: ${it.message}")
                    view.toast("Failed saving! >(")
                })
        )
    }
}
