package workshop.akbolatss.xchangesrates.presentation.chart

import android.graphics.Color
import android.os.Bundle
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
import kz.jgroup.pos.util.EventObserver
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.BaseFragment
import workshop.akbolatss.xchangesrates.databinding.FragmentChartBinding
import workshop.akbolatss.xchangesrates.domain.model.Chart
import workshop.akbolatss.xchangesrates.presentation.model.ChartPeriod
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.DateXValueFormatter
import workshop.akbolatss.xchangesrates.utils.extension.getThemeColor
import workshop.akbolatss.xchangesrates.utils.extension.showSnackBar

class ChartFragment(
    override val layoutId: Int = R.layout.fragment_chart
) : BaseFragment<FragmentChartBinding>() {

    companion object {

        fun newInstance(): ChartFragment {
            return ChartFragment()
        }
    }

    private val viewModel by currentScope.viewModel<ChartViewModel>(this)

    private lateinit var adapter: PeriodSelectorAdapter

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        binding.viewModel = viewModel

        onInitRV()
        onInitChart()
    }

    private fun onInitRV() {
        adapter = PeriodSelectorAdapter { historyButton, position ->
            highlightSelected(historyButton)
        }

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter
    }

    private fun highlightSelected(historyButton: ChartPeriod) {
        viewModel.toggleSelected(historyButton)
    }

    private fun onInitChart() {
        binding.lineChart.description.isEnabled = false
        binding.lineChart.legend.isEnabled = false
        binding.lineChart.setNoDataTextColor(
            _mActivity.getThemeColor(R.attr.colorError)
        )
        binding.lineChart.setMaxVisibleValueCount(16)

        val xAxisFormatter = DateXValueFormatter()
        val xAxis = binding.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelCount = 3
        xAxis.valueFormatter = xAxisFormatter

        xAxis.textColor = _mActivity.getThemeColor(R.attr.colorOnPrimary)
        xAxis.setDrawGridLines(false)

        val yAxis = binding.lineChart.axisLeft
        yAxis.textColor = _mActivity.getThemeColor(R.attr.colorOnPrimary)

        val yAxis1 = binding.lineChart.axisRight
        yAxis1.isEnabled = false
    }

    override fun setObserversListeners() {
        observeViewModel()
        setListeners()
    }

    private fun observeViewModel() {
        viewModel.chartPeriodList.observe(viewLifecycleOwner, Observer { chartPeriodList ->
            adapter.submitList(chartPeriodList)
        })
        viewModel.selectedPeriod.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.tryLoadChart()
            }
        })
        viewModel.chart.observe(viewLifecycleOwner, Observer { chart ->
            chart?.let {
                loadLineChart(chart)
            }
        })
        viewModel.currencyError.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.coordinator.showSnackBar(it)
            }
        })
        viewModel.coinError.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.coordinator.showSnackBar(it)
            }
        })
        viewModel.snapshotCreated.observe(viewLifecycleOwner, EventObserver {
            binding.coordinator.showSnackBar(it)
        })
        viewModel.snapshotCreatedError.observe(viewLifecycleOwner, EventObserver {
            binding.coordinator.showSnackBar(it)
        })
    }

    private fun setListeners() {

    }

    private fun loadLineChart(chart: Chart) {
        lifecycleScope.launch(Dispatchers.IO) {
            val barEntries = chart.units.map { unit ->
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
                color = _mActivity.getThemeColor(R.attr.colorAccent)
                circleRadius = 1.4f
                setCircleColor(Color.WHITE)
                valueTextColor = Color.WHITE
                fillColor = _mActivity.getThemeColor(R.attr.colorPrimary)
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

    fun onSaveSnapshot() {
        viewModel.tryCreateSnapshot()
    }

    /**
     * Helper showcase. Used only on first opening this screen
     */
    fun showStartupShowCase() {
        if (!Hawk.get(Constants.HAWK_SHOWCASE_1_DONE, false)) {
            val showCaseQueue: FancyShowCaseQueue

            val showCase1 = FancyShowCaseView.Builder(_mActivity)
                .title(resources.getString(R.string.showcase_chart_1))
//                .backgroundColor(R.color.colorShowCaseBG)
                .build()

            val showCase2 = FancyShowCaseView.Builder(_mActivity)
//                .focusOn(binding.tvExchanger)
                .title(resources.getString(R.string.showcase_chart_2))
//                .backgroundColor(R.color.colorShowCaseBG)
                .build()

            val showCase3 = FancyShowCaseView.Builder(_mActivity)
//                .focusOn(binding.tvCoin)
                .title(resources.getString(R.string.showcase_chart_3))
//                .backgroundColor(R.color.colorShowCaseBG)
                .build()

            val showCase4 = FancyShowCaseView.Builder(_mActivity)
//                .focusOn(binding.tvCurrency)
                .title(resources.getString(R.string.showcase_chart_4))
//                .backgroundColor(R.color.colorShowCaseBG)
                .build()

            val showCase5 = FancyShowCaseView.Builder(_mActivity)
                .focusOn(binding.recyclerView)
                .title(resources.getString(R.string.showcase_chart_5))
//                .backgroundColor(R.color.colorShowCaseBG)
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
