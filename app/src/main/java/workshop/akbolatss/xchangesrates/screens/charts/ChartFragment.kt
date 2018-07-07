package workshop.akbolatss.xchangesrates.screens.charts

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.fragment_main.*
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.SupportFragment
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.app.ApplicationMain
import workshop.akbolatss.xchangesrates.model.response.ChartResponseData
import workshop.akbolatss.xchangesrates.model.response.ExchangeResponse
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository
import workshop.akbolatss.xchangesrates.screens.main.MainActivity
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.DateXValueFormatter
import java.util.*

class ChartFragment : SupportFragment(), AdapterView.OnItemSelectedListener, HorizontalBtnsAdapter.onBtnClickListener, ChartView, ISupportFragment, View.OnFocusChangeListener, TextWatcher {


    private var mPresenter: ChartPresenter? = null

    private var mBtnsAdapter: HorizontalBtnsAdapter? = null

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

    companion object {

        fun newInstance(): ChartFragment {
            return ChartFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        mPresenter = ChartPresenter(DBChartRepository((activity!!.application as ApplicationMain).daoSession,
                ApplicationMain.apiService))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mPresenter != null) {
            mPresenter!!.onViewAttached(this)
        }
        onInitRV()
        onInitChart()
        onInitSpinner()

        if (!Hawk.get(Constants.HAWK_SHOWCASE_1_DONE, false)) {
            val showCaseQueue: FancyShowCaseQueue

            val showCase1 = FancyShowCaseView.Builder(activity!!)
                    .title(resources.getString(R.string.showcase_chart_1))
                    .backgroundColor(R.color.colorShowCaseBG)
                    .build()

            val showCase2 = FancyShowCaseView.Builder(activity!!)
                    .focusOn(spinExchanges)
                    .title(resources.getString(R.string.showcase_chart_2))
                    .backgroundColor(R.color.colorShowCaseBG)
                    .build()

            val showCase3 = FancyShowCaseView.Builder(activity!!)
                    .focusOn(spinCoin)
                    .title(resources.getString(R.string.showcase_chart_3))
                    .backgroundColor(R.color.colorShowCaseBG)
                    .build()

            val showCase4 = FancyShowCaseView.Builder(activity!!)
                    .focusOn(spinCurrencies)
                    .title(resources.getString(R.string.showcase_chart_4))
                    .backgroundColor(R.color.colorShowCaseBG)
                    .build()

            val showCase5 = FancyShowCaseView.Builder(activity!!)
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

            showCaseQueue.setCompleteListener { (activity as MainActivity).onShowCase1() }

            showCaseQueue.show()
            Hawk.put(Constants.HAWK_SHOWCASE_1_DONE, true)
        }
    }

    private fun onInitSpinner() {
        mExchangeResponse = Hawk.get(Constants.HAWK_EXCHANGE_RESPONSE)

        val ids = arrayOfNulls<String>(mExchangeResponse!!.data.size)
        for (i in 0 until mExchangeResponse!!.data.size) {
            ids[i] = mExchangeResponse!!.data[i].caption
        }

        val arrayAdapter = ArrayAdapter<String>(_mActivity, R.layout.custom_spinner_item, ids)
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        spinExchanges.adapter = arrayAdapter
        spinExchanges.onItemSelectedListener = this@ChartFragment
        spinCoin.onItemSelectedListener = this@ChartFragment // Use in this order!
        spinCurrencies.onItemSelectedListener = this@ChartFragment // !!!

        etCoin.addTextChangedListener(this)
        etCurrency.addTextChangedListener(this)

        etCoin.isFocusable = false
        etCurrency.isFocusable = false

        etCoin.onFocusChangeListener = this@ChartFragment
        etCurrency.onFocusChangeListener = this@ChartFragment
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

        val selectedHistory = Hawk.get<Int>(Constants.HAWK_HISTORY_POS).toInt()
        mPresenter!!.mTerm = Hawk.get(Constants.HAWK_HISTORY_CODE, Constants.HOUR_24)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        mBtnsAdapter = HorizontalBtnsAdapter(strings, selectedHistory, this)
        recyclerView.adapter = mBtnsAdapter
        recyclerView.smoothScrollToPosition(selectedHistory)
    }

    override fun onFocusChange(view: View, hasFocus: Boolean) {
        if (view.id == R.id.etCoin) {
            isCoinEtFocused = hasFocus
            isCurrencyEtFocused = !hasFocus
        } else if (view.id == R.id.etCurrency) {
            isCoinEtFocused = !hasFocus
            isCurrencyEtFocused = hasFocus
        }
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

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        if (adapterView.id == spinCurrencies!!.id) {
            mPresenter!!.mCurrencyCode = adapterView.selectedItem.toString()
            mPresenter!!.onUpdate()
        } else if (adapterView.id == spinCoin!!.id) {
            mPresenter!!.mCoinCode = adapterView.selectedItem.toString()

            val strings = mPresenter!!.exchangeModel!!.currencies[mPresenter!!.exchangeModel!!.currencies.keys.toTypedArray()[i]]
            strings!!.sorted()

            val arrayAdapter = ArrayAdapter(adapterView.context, R.layout.custom_spinner_item, strings)
            arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
            spinCurrencies!!.adapter = arrayAdapter
        } else if (adapterView.id == spinExchanges!!.id) {

            mPresenter!!.exchangeModel = mExchangeResponse!!.data[i]

            val arrayAdapter = ArrayAdapter<Any>(adapterView.context, R.layout.custom_spinner_item,
                    mPresenter!!.exchangeModel!!.currencies.keys.toTypedArray())
            arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
            spinCoin.adapter = arrayAdapter
            spinCoin.setSelection(0, false) // TODO: To Prevent Double CALL
        }
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {}

    override fun onLoadLineChart(chartData: ChartResponseData) {
        mSelectedCurrencyRate = java.lang.Float.parseFloat(chartData.info.last)

        etCoin!!.setText(1.toString())
        etCurrency!!.setText(chartData.info.last)

        val entries = ArrayList<Entry>()
        for (i in 0 until chartData.chart.size) {
            entries.add(BarEntry(chartData.chart[i].timestamp.toFloat(),
                    java.lang.Float.parseFloat(chartData.chart[i].price)))
        }

        val set = LineDataSet(entries, "")
        set.setDrawFilled(true)
        set.setDrawCircles(true)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.cubicIntensity = 0.2f
        set.lineWidth = 1.8f
        set.color = ContextCompat.getColor(_mActivity, R.color.colorAccent)
        set.circleRadius = 2f
        set.setCircleColor(Color.WHITE)
        set.valueTextColor = Color.WHITE
        set.fillColor = ContextCompat.getColor(_mActivity, R.color.colorPrimaryDark)
        set.fillAlpha = 100

        val lineData = LineData(set)
        lineData.setValueTextSize(9f)
        lineData.isHighlightEnabled = false

        lineChart.data = lineData
        lineChart.invalidate()
    }

    override fun onBtnClick(id: String, pos: Int) {
        mPresenter!!.mTerm = id
        mPresenter!!.onUpdate()
        Hawk.put(Constants.HAWK_HISTORY_CODE, id)
        Hawk.put(Constants.HAWK_HISTORY_POS, pos)
    }

    fun onUpdate() {
        mPresenter!!.onUpdate()
    }

    fun onSaveSnapshot() {
        Toast.makeText(_mActivity, R.string.toast_saving, Toast.LENGTH_SHORT).show()
        mPresenter!!.onSaveSnap()
    }

    override fun onPause() {
        super.onPause()
        hideSoftInput()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mPresenter != null) {
            mPresenter!!.onViewDetached(this)
        }
    }
}
