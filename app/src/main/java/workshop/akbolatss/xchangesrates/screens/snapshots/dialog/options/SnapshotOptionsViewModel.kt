package workshop.akbolatss.xchangesrates.screens.snapshots.dialog.options

import androidx.lifecycle.MutableLiveData
import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.base.resource.onFailure
import workshop.akbolatss.xchangesrates.base.resource.onSuccess
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.usecase.FindSnapshotByIdUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.UpdateSnapshotOptionsUseCase
import workshop.akbolatss.xchangesrates.presentation.base.Error
import workshop.akbolatss.xchangesrates.presentation.base.ViewState

class SnapshotOptionsViewModel(
    private val findSnapshotByIdUseCase: FindSnapshotByIdUseCase,
    private val updateSnapshotOptionsUseCase: UpdateSnapshotOptionsUseCase,
    private val snapshotId: Long
) : BaseViewModel() {

    val snapshotError = MutableLiveData<ViewState>()

    val chartChangesPeriod = MutableLiveData<Int>()
    val selectedChangesPeriod = MutableLiveData<String>()

    val updateInterval = MutableLiveData<Int>()
    val selectedUpdateInterval = MutableLiveData<String>()

    val isNotificationEnabled = MutableLiveData<Boolean>()
    val isStickNotification = MutableLiveData<Boolean>()

    val optionsUpdated = MutableLiveData<Boolean>()

    init {
        loadSnapshot()
    }

    private fun loadSnapshot() {
        executeUseCase { scope ->
            findSnapshotByIdUseCase(scope, FindSnapshotByIdUseCase.Params(snapshotId))
                .onSuccess { snapshot ->
                    initUi(snapshot)
                }
                .onFailure { failure ->
                    snapshotError.value = Error(failure)
                }
        }
    }

    private fun initUi(snapshot: Snapshot) {
        isNotificationEnabled.value = snapshot.options.isNotificationEnabled
        isStickNotification.value = snapshot.options.isStick
        chartChangesPeriod.value = snapshot.options.changesForPeriod.indexUi
        updateInterval.value = snapshot.options.updateInterval.indexUi
    }

    fun save() {
        val isNotificationEnabled = isNotificationEnabled.value ?: false
        val isStickNotification = isStickNotification.value ?: false
        val chartChangesPeriodIndex = chartChangesPeriod.value ?: 3
        val updateIntervalIndex = updateInterval.value ?: 3

        executeUseCase { scope ->
            updateSnapshotOptionsUseCase(
                scope, UpdateSnapshotOptionsUseCase.Params(
                    snapshotId,
                    isNotificationEnabled,
                    isStickNotification,
                    chartChangesPeriodIndex,
                    updateIntervalIndex
                )
            ).onSuccess {
                optionsUpdated.value = true
            }
        }
    }

}
