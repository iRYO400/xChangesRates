package workshop.akbolatss.xchangesrates.presentation.chart

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.orhanobut.hawk.Hawk
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.BaseFragment
import workshop.akbolatss.xchangesrates.data.remote.model.StatsResponse
import workshop.akbolatss.xchangesrates.databinding.FragmentChartBinding
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.model.response.ChartItem
import workshop.akbolatss.xchangesrates.presentation.chart.dialog.PICKER_TYPE
import workshop.akbolatss.xchangesrates.presentation.chart.dialog.PickerDialog
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.DateXValueFormatter
import workshop.akbolatss.xchangesrates.utils.UtilityMethods

class ChartFragment(
    override val layoutId: Int = R.layout.fragment_chart
) : BaseFragment<FragmentChartBinding>(),
    HorizontalBtnsAdapter.OnBtnClickListener,
    PickerDialog.PickerDialogListener,
    ChartView,
    TextWatcher {

    private val viewModel by currentScope.viewModel<ChartViewModel>(this)

    private lateinit var mPresenter: ChartPresenter

    private lateinit var mBtnsAdapter: HorizontalBtnsAdapter

    private var mExchangeResponse: StatsResponse? = null

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

    companion object {

        fun newInstance(): ChartFragment {
            return ChartFragment()
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

        onInitRV()
        onInitChart()
        onInitSpinner()
    }

    private fun onInitRV() {
        val strings =
            generateButtons()

        val selectedHistory = Hawk.get<Int>(Constants.HAWK_HISTORY_POS).toInt()
        mPresenter.mTerm = Hawk.get(Constants.HAWK_HISTORY_CODE, Constants.HOUR_24)

        mBtnsAdapter =
            HorizontalBtnsAdapter(
                strings,
                selectedHistory,
                this
            )
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = mBtnsAdapter
        binding.recyclerView.scrollToPosition(selectedHistory)
    }

    private fun onInitChart() {
        binding.lineChart.description.isEnabled = false
        binding.lineChart.legend.isEnabled = false
        binding.lineChart.setNoDataTextColor(
            ContextCompat.getColor(
                _mActivity,
                R.color.colorSpinTxt
            )
        )
        binding.lineChart.setMaxVisibleValueCount(16)

        val xAxisFormatter = DateXValueFormatter()
        val xAxis = binding.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelCount = 3
        xAxis.valueFormatter = xAxisFormatter
        xAxis.textColor = ContextCompat.getColor(_mActivity, R.color.colorInactive)
        xAxis.setDrawGridLines(false)

        val yAxis = binding.lineChart.axisLeft
        yAxis.textColor = ContextCompat.getColor(_mActivity, R.color.colorInactive)

        val yAxis1 = binding.lineChart.axisRight
        yAxis1.isEnabled = false
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

        binding.tvExchanger.setOnClickListener {
            val fm = fragmentManager
            val dialogFragment =
                PickerDialog.newInstance(
                    PICKER_TYPE.EXCHANGER,
                    exchangerCodes,
                    mPresenter.exchangeModelPos
                )
            dialogFragment.setTargetFragment(this@ChartFragment, 100)
            dialogFragment.show(fm!!, "fm")
        }

        binding.tvCoin.setOnClickListener {
            val arrayList = mPresenter.exchangeModel.currencies.keys.toMutableList()
            val fm = fragmentManager
            val dialogFragment =
                PickerDialog.newInstance(
                    PICKER_TYPE.COIN,
                    arrayList as ArrayList<String>,
                    mPresenter.coinCodePos
                )
            dialogFragment.setTargetFragment(this@ChartFragment, 200)
            dialogFragment.show(fm!!, "fm")
        }

        binding.tvCurrency.setOnClickListener {
            val arrayList = mPresenter.exchangeModel.currencies.getValue(mPresenter.mCoinCode)
            val fm = fragmentManager
            val dialogFragment =
                PickerDialog.newInstance(
                    PICKER_TYPE.CURRENCY,
                    arrayList as ArrayList<String>,
                    mPresenter.currencyCodePos
                )
            dialogFragment.setTargetFragment(this@ChartFragment, 300)
            dialogFragment.show(fm!!, "fm")
        }

        binding.etCoin.addTextChangedListener(this)
        binding.etCurrency.addTextChangedListener(this)

        binding.etCoin.isFocusable = false
        binding.etCurrency.isFocusable = false

        binding.etCoin.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            isCoinEtFocused = hasFocus
            isCurrencyEtFocused = !hasFocus
        }
        binding.etCurrency.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            isCoinEtFocused = !hasFocus
            isCurrencyEtFocused = hasFocus
        }
    }

    override fun setObserversListeners() {

    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (isCoinEtFocused && s.isNotEmpty()) {
            val result = s.toString().toFloat() * mSelectedCurrencyRate
            binding.etCurrency.setText(result.toString())
        }
        if (isCurrencyEtFocused && s.isNotEmpty()) {
            val result = s.toString().toFloat() * mSelectedCurrencyRate
            binding.etCoin.setText(result.toString())
        }
    }


    /**
     * NumberPicker Dialog listener
     */
    override fun itemSelected(intValue: Int, stringValue: String, pickType: PICKER_TYPE) {
        when (pickType) {
            PICKER_TYPE.EXCHANGER -> {
                mPresenter.exchangeModel = mExchangeResponse!!.data[intValue]
                binding.tvExchanger.text = mPresenter.exchangeModel.caption

                val coin = mPresenter.exchangeModel.currencies.keys.first()
                mPresenter.mCoinCode = coin
                binding.tvCoin.text = coin

                mPresenter.mCurrencyCode = mPresenter.exchangeModel.currencies[coin]!![0]
                binding.tvCurrency.text = mPresenter.exchangeModel.currencies[coin]!![0]

                mPresenter.exchangeModelPos = intValue
            }
            PICKER_TYPE.COIN -> {
                mPresenter.mCoinCode = stringValue
                binding.tvCoin.text = stringValue

                mPresenter.mCurrencyCode = mPresenter.exchangeModel.currencies[stringValue]!![0]
                binding.tvCurrency.text = mPresenter.exchangeModel.currencies[stringValue]!![0]

                mPresenter.coinCodePos = intValue
            }
            PICKER_TYPE.CURRENCY -> {
                mPresenter.mCurrencyCode = stringValue
                binding.tvCurrency.text = stringValue

                mPresenter.currencyCodePos = intValue
            }
        }

        mPresenter.onUpdate()
    }

    override fun onLoadLineChart(chartData: ChartData) {
        mSelectedCurrencyRate = chartData.info.last!!.toFloat()

        binding.etCoin.setText(1.toString())
        binding.etCurrency.setText(chartData.info.last)

        fillLineData(chartData.chart)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { lineData ->
                binding.lineChart.data = lineData
                binding.lineChart.invalidate()
            }
    }

    private fun fillLineData(chartsList: List<ChartItem>): Single<LineData> {
        return Observable.fromIterable(chartsList)
            .map { chartItem ->
                BarEntry(
                    chartItem.timestamp!!.toFloat(),
                    chartItem.price!!.toFloat()
                )
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
        hideSoftInput() //Pos.RootActivity.override birnase
    }

    override fun onSaveSnapshot(isSuccess: Boolean, chartData: ChartData) {
        if (isSuccess)
            UtilityMethods.createNotificationChannel(chartData, _mActivity)
    }

    override fun toast(message: String) {
        Toast.makeText(_mActivity, message, Toast.LENGTH_SHORT).show()
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
                .focusOn(binding.tvExchanger)
                .title(resources.getString(R.string.showcase_chart_2))
                .backgroundColor(R.color.colorShowCaseBG)
                .build()

            val showCase3 = FancyShowCaseView.Builder(_mActivity)
                .focusOn(binding.tvCoin)
                .title(resources.getString(R.string.showcase_chart_3))
                .backgroundColor(R.color.colorShowCaseBG)
                .build()

            val showCase4 = FancyShowCaseView.Builder(_mActivity)
                .focusOn(binding.tvCurrency)
                .title(resources.getString(R.string.showcase_chart_4))
                .backgroundColor(R.color.colorShowCaseBG)
                .build()

            val showCase5 = FancyShowCaseView.Builder(_mActivity)
                .focusOn(binding.recyclerView)
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
}
