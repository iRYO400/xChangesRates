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
import workshop.akbolatss.xchangesrates.utils.Constants.HAWK_IS_INTRO_START_DONE
import workshop.akbolatss.xchangesrates.utils.chart.setupLineChartInChartOverview
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
        binding.lineChart.setupLineChartInChartOverview(_mActivity)
    }

    override fun setObserversListeners() {
        observeViewModel()
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
        viewModel.currency.observe(viewLifecycleOwner, Observer {
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

    fun onSaveSnapshot() {
        viewModel.tryCreateSnapshot()
    }

    override fun onResume() {
        super.onResume()
        showStartupShowCase()
    }

    /**
     * Helper showcase. Used only on first opening this screen
     */
    private fun showStartupShowCase() {
        if (!Hawk.get(HAWK_IS_INTRO_START_DONE, false)) {
            val showCase0 = FancyShowCaseView.Builder(_mActivity)
                .title(resources.getString(R.string.showcase_chart_0))
                .backgroundColor(R.color.big_stone_trans)
                .enableAutoTextPosition()
                .build()

            val showCase1 = FancyShowCaseView.Builder(_mActivity)
                .title(resources.getString(R.string.showcase_chart_1))
                .backgroundColor(R.color.big_stone_trans)
                .enableAutoTextPosition()
                .build()

            val showCase2 = FancyShowCaseView.Builder(_mActivity)
                .title(resources.getString(R.string.showcase_chart_2))
                .focusOn(binding.actvExchanger)
                .backgroundColor(R.color.big_stone_trans)
                .enableAutoTextPosition()
                .build()

            val showCase3 = FancyShowCaseView.Builder(_mActivity)
                .title(resources.getString(R.string.showcase_chart_3))
                .focusOn(binding.actvCoin)
                .backgroundColor(R.color.big_stone_trans)
                .enableAutoTextPosition()
                .build()

            val showCase4 = FancyShowCaseView.Builder(_mActivity)
                .title(resources.getString(R.string.showcase_chart_4))
                .focusOn(binding.actvCurrency)
                .backgroundColor(R.color.big_stone_trans)
                .enableAutoTextPosition()
                .build()

            val showCase5 = FancyShowCaseView.Builder(_mActivity)
                .title(resources.getString(R.string.showcase_chart_5))
                .backgroundColor(R.color.big_stone_trans)
                .enableAutoTextPosition()
                .build()

            val showCase6 = FancyShowCaseView.Builder(_mActivity)
                .title(resources.getString(R.string.showcase_chart_6))
                .backgroundColor(R.color.big_stone_trans)
                .enableAutoTextPosition()
                .build()

            val showCase7 = FancyShowCaseView.Builder(_mActivity)
                .title(resources.getString(R.string.showcase_chart_7))
                .backgroundColor(R.color.big_stone_trans)
                .enableAutoTextPosition()
                .build()

            FancyShowCaseQueue()
                .add(showCase0)
                .add(showCase1)
                .add(showCase2)
                .add(showCase3)
                .add(showCase4)
                .add(showCase5)
                .add(showCase6)
                .add(showCase7)
                .show()

            Hawk.put(HAWK_IS_INTRO_START_DONE, true)
        }
    }
}
