package workshop.akbolatss.xchangesrates.presentation.chart

import android.os.Bundle
import androidx.lifecycle.Observer
import com.orhanobut.hawk.Hawk
import kz.jgroup.pos.util.EventObserver
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.BaseFragment
import workshop.akbolatss.xchangesrates.databinding.FragmentChartBinding
import workshop.akbolatss.xchangesrates.presentation.model.ChartPeriod
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.chart.setupChart
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
        binding.viewLifecycleOwner = viewLifecycleOwner

        onInitRV()
        onInitChart()
    }

    private fun onInitRV() {
        adapter = PeriodSelectorAdapter { historyButton, _ ->
            highlightSelected(historyButton)
        }
        binding.recyclerView.adapter = adapter
    }

    private fun highlightSelected(historyButton: ChartPeriod) {
        viewModel.toggleSelected(historyButton)
    }

    private fun onInitChart() {
        binding.lineChart.setupChart(_mActivity)
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
        viewModel.chartError.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.coordinator.showSnackBar(it)
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
