package workshop.akbolatss.xchangesrates.screens.charts

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.orhanobut.hawk.Hawk
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import me.yokeyword.fragmentation.SupportFragment
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.app.ApplicationMain
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.model.response.ChartItem
import workshop.akbolatss.xchangesrates.model.response.ExchangeResponse
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.DateXValueFormatter
import workshop.akbolatss.xchangesrates.utils.UtilityMethods

class ChartFragment : SupportFragment(), HorizontalBtnsAdapter.OnBtnClickListener, PickerDialog.PickerDialogListener, ChartView, TextWatcher {

    private lateinit var mPresenter: ChartPresenter

    private lateinit var mBtnsAdapter: HorizontalBtnsAdapter

    private var mExchangeResponse: ExchangeResponse? = null
    /**
     * Коэффициент курса выбранной валюты
     */
    private var mSelectedCurrencyRate: Float = 0.toFloat()
    /**
     * Фокус на mEtCoin
     */
    private var isCoinEtFocused: Boolean = false
    /**
     * Фокус на mEtCurrency
     */
    private var isCurrencyEtFocused: Boolean = false

    private lateinit var mDisposable: Disposable

    companion object {

        fun newInstance(): ChartFragment {
            return ChartFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        mPresenter = ChartPresenter(DBChartRepository(ApplicationMain.apiService,
                ApplicationMain.instance.appDatabase.chartDataDao()))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.onViewAttached(this)

        onInitRV()
        onInitChart()
        onInitSpinner()
    }

    private fun onInitSpinner() {
        mExchangeResponse = Hawk.get(Constants.HAWK_EXCHANGE_RESPONSE)
        mPresenter.exchangeModel = mExchangeResponse!!.data[0]
        mPresenter.mCoinCode = mPresenter.exchangeModel.currencies.keys.first()
        mPresenter.mCurrencyCode = mPresenter.exchangeModel.currencies[mPresenter.mCoinCode]!![0]
        mPresenter.onUpdate()

        val exchangerCodes = ArrayList<String>()
        for (exchanger in mExchangeResponse!!.data) {
            exchangerCodes.add(exchanger.caption)
        }

        tvExchanger.setOnClickListener {
            val fm = fragmentManager
            val dialogFragment = PickerDialog.newInstance(PICKER_TYPE.EXCHANGER, exchangerCodes, mPresenter.exchangeModelPos)
            dialogFragment.setTargetFragment(this@ChartFragment, 100)
            dialogFragment.show(fm!!, "fm")
        }

        tvCoin.setOnClickListener {
            val arrayList = mPresenter.exchangeModel.currencies.keys.toMutableList()
            val fm = fragmentManager
            val dialogFragment = PickerDialog.newInstance(PICKER_TYPE.COIN, arrayList as ArrayList<String>, mPresenter.coinCodePos)
            dialogFragment.setTargetFragment(this@ChartFragment, 200)
            dialogFragment.show(fm!!, "fm")
        }

        tvCurrency.setOnClickListener {
            val arrayList = mPresenter.exchangeModel.currencies.getValue(mPresenter.mCoinCode)
            val fm = fragmentManager
            val dialogFragment = PickerDialog.newInstance(PICKER_TYPE.CURRENCY, arrayList as ArrayList<String>, mPresenter.currencyCodePos)
            dialogFragment.setTargetFragment(this@ChartFragment, 300)
            dialogFragment.show(fm!!, "fm")
        }

        etCoin.addTextChangedListener(this)
        etCurrency.addTextChangedListener(this)

        etCoin.isFocusable = false
        etCurrency.isFocusable = false

        etCoin.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            isCoinEtFocused = hasFocus
            isCurrencyEtFocused = !hasFocus
        }
        etCurrency.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            isCoinEtFocused = !hasFocus
            isCurrencyEtFocused = hasFocus
        }
    }


    private fun onInitChart() {
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.setNoDataTextColor(ContextCompat.getColor(_mActivity, R.color.colorSpinTxt))
        lineChart.setMaxVisibleValueCount(16)

        val xAxisFormatter = DateXValueFormatter()
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelCount = 3
        xAxis.valueFormatter = xAxisFormatter
        xAxis.textColor = ContextCompat.getColor(_mActivity, R.color.colorInactive)
        xAxis.setDrawGridLines(false)

        val yAxis = lineChart.axisLeft
        yAxis.textColor = ContextCompat.getColor(_mActivity, R.color.colorInactive)

        val yAxis1 = lineChart.axisRight
        yAxis1.isEnabled = false
    }

    /**
     * Init history buttons
     */
    private fun onInitRV() {
        val strings = mPresenter.generateButtons()

        val selectedHistory = Hawk.get<Int>(Constants.HAWK_HISTORY_POS).toInt()
        mPresenter.mTerm = Hawk.get(Constants.HAWK_HISTORY_CODE, Constants.HOUR_24)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        mBtnsAdapter = HorizontalBtnsAdapter(strings, selectedHistory, this)
        recyclerView.adapter = mBtnsAdapter
        recyclerView.scrollToPosition(selectedHistory)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (isCoinEtFocused && s.isNotEmpty()) {
            val result = s.toString().toFloat() * mSelectedCurrencyRate
            etCurrency.setText(result.toString())
        }
        if (isCurrencyEtFocused && s.isNotEmpty()) {
            val result = s.toString().toFloat() * mSelectedCurrencyRate
            etCoin.setText(result.toString())
        }
    }


    /**
     * NumberPicker Dialog listener
     */
    override fun itemSelected(intValue: Int, stringValue: String, pickType: PICKER_TYPE) {
        when (pickType) {
            PICKER_TYPE.EXCHANGER -> {
                mPresenter.exchangeModel = mExchangeResponse!!.data[intValue]
                tvExchanger.text = mPresenter.exchangeModel.caption

                val coin = mPresenter.exchangeModel.currencies.keys.first()
                mPresenter.mCoinCode = coin
                tvCoin.text = coin

                mPresenter.mCurrencyCode = mPresenter.exchangeModel.currencies[coin]!![0]
                tvCurrency.text = mPresenter.exchangeModel.currencies[coin]!![0]

                mPresenter.exchangeModelPos = intValue
            }
            PICKER_TYPE.COIN -> {
                mPresenter.mCoinCode = stringValue
                tvCoin.text = stringValue

                mPresenter.mCurrencyCode = mPresenter.exchangeModel.currencies[stringValue]!![0]
                tvCurrency.text = mPresenter.exchangeModel.currencies[stringValue]!![0]

                mPresenter.coinCodePos = intValue
            }
            PICKER_TYPE.CURRENCY -> {
                mPresenter.mCurrencyCode = stringValue
                tvCurrency.text = stringValue

                mPresenter.currencyCodePos = intValue
            }
        }

        mPresenter.onUpdate()
    }


    /**
     * Helper showcase. Used only on first opening this screen
     */
    fun showStartupShowCase() {
        if (!Hawk.get(Constants.HAWK_SHOWCASE_1_DONE, false)) {
            val showCaseQueue: FancyShowCaseQueue

            val showCase1 = FancyShowCaseView.Builder(_mActivity)
                    .title(resources.getString(R.string.showcase_chart_1))
                    .backgroundColor(R.color.colorShowCaseBG)
                    .build()

            val showCase2 = FancyShowCaseView.Builder(_mActivity)
                    .focusOn(tvExchanger)
                    .title(resources.getString(R.string.showcase_chart_2))
                    .backgroundColor(R.color.colorShowCaseBG)
                    .build()

            val showCase3 = FancyShowCaseView.Builder(_mActivity)
                    .focusOn(tvCoin)
                    .title(resources.getString(R.string.showcase_chart_3))
                    .backgroundColor(R.color.colorShowCaseBG)
                    .build()

            val showCase4 = FancyShowCaseView.Builder(_mActivity)
                    .focusOn(tvCurrency)
                    .title(resources.getString(R.string.showcase_chart_4))
                    .backgroundColor(R.color.colorShowCaseBG)
                    .build()

            val showCase5 = FancyShowCaseView.Builder(_mActivity)
                    .focusOn(recyclerView)
                    .title(resources.getString(R.string.showcase_chart_5))
                    .backgroundColor(R.color.colorShowCaseBG)
                    .build()

            showCaseQueue = FancyShowCaseQueue()
                    .add(showCase1)
                    .add(showCase2)
                    .add(showCase3)
                    .add(showCase4)
                    .add(showCase5)

//            showCaseQueue.setCompleteListener { (activity as MainActivity).onShowCase1() }

            showCaseQueue.show()
            Hawk.put(Constants.HAWK_SHOWCASE_1_DONE, true)
        }
    }

    override fun onLoadLineChart(chartData: ChartData) {
        mSelectedCurrencyRate = chartData.info.last!!.toFloat()

        etCoin!!.setText(1.toString())
        etCurrency!!.setText(chartData.info.last)

        mDisposable = fillLineData(chartData.chart)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { lineData ->
                    lineChart.data = lineData
                    lineChart.invalidate()
                }
    }

    private fun fillLineData(chartsList: List<ChartItem>): Single<LineData> {
        return Observable.fromIterable(chartsList)
                .map { chartItem ->
                    BarEntry(chartItem.timestamp!!.toFloat(),
                            chartItem.price!!.toFloat())
                }
                .toList()
                .map {
                    val dataSet = LineDataSet(it as List<Entry>, "")
                    dataSet.setDrawFilled(true)
                    dataSet.setDrawCircles(true)
                    dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                    dataSet.cubicIntensity = 0.2f
                    dataSet.lineWidth = 1.8f
                    dataSet.color = ContextCompat.getColor(_mActivity, R.color.colorAccent)
                    dataSet.circleRadius = 1.4f
                    dataSet.setCircleColor(Color.WHITE)
                    dataSet.valueTextColor = Color.WHITE
                    dataSet.fillColor = ContextCompat.getColor(_mActivity, R.color.colorPrimaryDark)
                    dataSet.fillAlpha = 100
                    dataSet
                }
                .map { dataSet ->
                    val lineData = LineData(dataSet)
                    lineData.setValueTextSize(9f)
                    lineData.isHighlightEnabled = false
                    lineData
                }
    }

    override fun onBtnClick(id: String, pos: Int) {
        mPresenter.mTerm = id
        mPresenter.onUpdate()
        Hawk.put(Constants.HAWK_HISTORY_CODE, id)
        Hawk.put(Constants.HAWK_HISTORY_POS, pos)
    }

    fun onSaveSnapshot() {
        mPresenter.saveChartData()
    }

    override fun onPause() {
        super.onPause()
        hideSoftInput()
    }

    override fun onSaveSnapshot(isSuccess: Boolean, chartData: ChartData) {
        if (isSuccess)
            UtilityMethods.createNotificationChannel(chartData, _mActivity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::mDisposable.isInitialized)
            mDisposable.dispose()
        if (::mPresenter.isInitialized) {
            mPresenter.onViewDetached(this)
        }
    }

    override fun toast(message: String) {
        Toast.makeText(_mActivity, message, Toast.LENGTH_SHORT).show()
    }
}