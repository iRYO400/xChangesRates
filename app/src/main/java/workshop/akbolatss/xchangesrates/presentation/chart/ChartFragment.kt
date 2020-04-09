package workshop.akbolatss.xchangesrates.presentation.chart

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.BaseFragment
import workshop.akbolatss.xchangesrates.data.persistent.model.Chart
import workshop.akbolatss.xchangesrates.databinding.FragmentChartBinding
import workshop.akbolatss.xchangesrates.presentation.chart.PeriodSelectorAdapter.PeriodSelectorPayload.ITEM_SELECTED
import workshop.akbolatss.xchangesrates.presentation.chart.PeriodSelectorAdapter.PeriodSelectorPayload.ITEM_UNSELECTED
import workshop.akbolatss.xchangesrates.presentation.model.ChartPeriod
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.DateXValueFormatter

class ChartFragment(
    override val layoutId: Int = R.layout.fragment_chart
) : BaseFragment<FragmentChartBinding>(),
    HorizontalBtnsAdapter.OnBtnClickListener {

    private val viewModel by currentScope.viewModel<ChartViewModel>(this)

    private lateinit var adapter: PeriodSelectorAdapter

    private lateinit var mBtnsAdapter: HorizontalBtnsAdapter

    /**
     * Коэффициент курса выбранной валюты
     */
    private var mSelectedCurrencyRate: Float = 0.toFloat()

    companion object {

        fun newInstance(): ChartFragment {
            return ChartFragment()
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        binding.viewModel = viewModel

        onInitRV()
        onInitChart()
    }

    private fun onInitRV() {
        adapter = PeriodSelectorAdapter { historyButton, position ->
            highlightSelected(historyButton, position)
        }

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter
    }

    private fun highlightSelected(historyButton: ChartPeriod, position: Int) {
        viewModel.toggleSelected(historyButton, position)

        adapter.notifyItemChanged(
            viewModel.selectedPeriodPosition,
            ITEM_SELECTED
        )

        adapter.notifyItemChanged(
            viewModel.selectedPeriodPreviousPosition,
            ITEM_UNSELECTED
        )
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

    override fun setObserversListeners() {
        observeViewModel()
        setListeners()
    }

    private fun observeViewModel() {
        viewModel.chartPeriodList.observe(viewLifecycleOwner, Observer {chartPeriodList ->
            adapter.selectedPeriodPosition = viewModel.selectedPeriodPosition
            adapter.submitList(chartPeriodList)
        })
        viewModel.chart.observe(viewLifecycleOwner, Observer { chart ->
            chart?.let {
                onLoadLineChart(chart)
            }
        })
        viewModel.currencyError.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.d("currencyError")
            }
        })
        viewModel.coinError.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.d("coinError")
            }
        })
    }

    private fun setListeners() {
//        binding.etCoin.doOnTextChanged { text, start, count, after ->
//            if (text.isNullOrBlank().not()) {
//                val result = text.toString().toFloat() * mSelectedCurrencyRate
//                binding.etCurrency.setText(result.toString())
//            }
//        }
//        binding.etCurrency.doOnTextChanged { text, start, count, after ->
//            if (text.isNullOrBlank().not()) {
//                val result = text.toString().toFloat() * mSelectedCurrencyRate
//                binding.etCoin.setText(result.toString())
//            }
//        }
    }

    private fun onLoadLineChart(chartData: Chart) {
        mSelectedCurrencyRate = chartData.info.last.toFloat()

        binding.etCoin.setText(1.toString())
        binding.etCurrency.setText(chartData.info.last.toEngineeringString())

        lifecycleScope.launch(Dispatchers.IO) {
            val barEntries = chartData.units.map { unit ->
                BarEntry(
                    unit.timestamp.toFloat(),
                    unit.price.toFloat()
                )
            }

            val dataSet = LineDataSet(barEntries, "LÆßEL").apply {
                setDrawFilled(true)
                setDrawCircles(true)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                cubicIntensity = 0.2f
                lineWidth = 1.8f
                color = ContextCompat.getColor(_mActivity, R.color.colorAccent)
                circleRadius = 1.4f
                setCircleColor(Color.WHITE)
                valueTextColor = Color.WHITE
                fillColor = ContextCompat.getColor(_mActivity, R.color.colorPrimaryDark)
                fillAlpha = 100
            }
            val lineData = LineData(dataSet).apply {
                setValueTextSize(9f)
                isHighlightEnabled = false
            }

            withContext(Dispatchers.Main) {
                binding.lineChart.data = lineData
                binding.lineChart.invalidate()
            }
        }
    }

    override fun onBtnClick(id: String, pos: Int) {
//        mPresenter.mTerm = id
//        mPresenter.onUpdate()
        Hawk.put(Constants.HAWK_HISTORY_CODE, id)
        Hawk.put(Constants.HAWK_HISTORY_POS, pos)
    }

    fun onSaveSnapshot() {
//        mPresenter.saveChartData()
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
