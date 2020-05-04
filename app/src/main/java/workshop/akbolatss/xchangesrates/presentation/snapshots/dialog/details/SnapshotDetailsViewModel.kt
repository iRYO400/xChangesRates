package workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.details

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.domain.model.ChartDot
import workshop.akbolatss.xchangesrates.domain.usecase.FindSnapshotByIdFlowUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.UpdateSnapshotByPeriodUseCase
import workshop.akbolatss.xchangesrates.presentation.base.ViewState
import workshop.akbolatss.xchangesrates.presentation.model.ChartPeriod
import workshop.akbolatss.xchangesrates.presentation.model.defaultChartPeriodList
import java.math.BigDecimal
import java.util.*

class SnapshotDetailsViewModel(
    findSnapshotByIdFlowUseCase: FindSnapshotByIdFlowUseCase,
    private val updateSnapshotUseCase: UpdateSnapshotByPeriodUseCase,
    val snapshotId: Long
) : BaseViewModel() {

    val snapshot = findSnapshotByIdFlowUseCase(FindSnapshotByIdFlowUseCase.Params(snapshotId))
        .asLiveData()

    val snapshotLoadState = MutableLiveData<ViewState>()
    val snapshotUpdateState = MutableLiveData<ViewState>()

    val selectedChartDotRate = MediatorLiveData<BigDecimal>()
    val selectedChartDotTime = MediatorLiveData<Date>()

    val charts = MediatorLiveData<List<ChartDot>>()

    val chartPeriodList = MutableLiveData<List<ChartPeriod>>()
    val selectedPeriod = MutableLiveData<ChartPeriod>()

    init {
        setObservers()
        initChartPeriod()
    }

    private fun setObservers() {
        charts.addSource(snapshot) {
            charts.value = it.chartDots
        }
        selectedChartDotRate.addSource(snapshot) {
            selectedChartDotRate.value = it.rate
        }
        selectedChartDotTime.addSource(snapshot) {
            selectedChartDotTime.value = it.updateTime
        }
    }

    private fun initChartPeriod() {
        chartPeriodList.value = defaultChartPeriodList
    }

    fun toggleSelected(chartPeriod: ChartPeriod) {
        chartPeriodList.value?.let {
            val updatedList = it.map { period ->
                if (period.code == chartPeriod.code)
                    period.copy(isSelected = true)
                else
                    period.copy(isSelected = false)
            }
            chartPeriodList.value = updatedList
            selectedPeriod.value = chartPeriod
        }
    }

    fun tryUpdateSnapshot() {
        val chartPeriod = selectedPeriod.value ?: return
        update(snapshotId, chartPeriod)
    }

    private fun update(
        itemId: Long,
        chartPeriod: ChartPeriod
    ) {
        executeUseCase(
            viewState = {
                snapshotUpdateState.value = it
            }
        ) { scope ->
            updateSnapshotUseCase(
                scope,
                UpdateSnapshotByPeriodUseCase.Params(itemId, chartPeriod.code)
            )
        }
    }

}
