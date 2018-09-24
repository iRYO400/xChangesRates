package workshop.akbolatss.xchangesrates.screens.charts

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import workshop.akbolatss.xchangesrates.base.BasePresenter
import workshop.akbolatss.xchangesrates.model.ExchangeModel
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.model.response.ChartOptions
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository
import workshop.akbolatss.xchangesrates.utils.Constants
import java.util.ArrayList

class ChartPresenter(private val mRepository: DBChartRepository) : BasePresenter<ChartView>() {

    /**
     * Main model
     */
    lateinit var exchangeModel: ExchangeModel
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
                mRepository.getQueryResult(mCoinCode,
                        exchangeModel.ident,
                        mCurrencyCode,
                        mTerm)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            mChartData = response.data
                            mChartData.coin = mCoinCode
                            view.onLoadLineChart(mChartData)
//                            view.onHideLoading()
                        }, {
                            //                            view.onHideLoading()
                        }))
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


    fun generateButtons(): ArrayList<String> {
        val strings = ArrayList<String>()
        strings.add(Constants.MINUTES_10)
        strings.add(Constants.HOUR_1)
        strings.add(Constants.HOUR_3)
        strings.add(Constants.HOUR_12)
        strings.add(Constants.HOUR_24)
        strings.add(Constants.WEEK)
        strings.add(Constants.MONTH)
        strings.add(Constants.MONTH_3)
        strings.add(Constants.MONTH_6)
        strings.add(Constants.YEAR_1)
        strings.add(Constants.YEAR_2)
        strings.add(Constants.YEAR_5)
        return strings
    }
}
