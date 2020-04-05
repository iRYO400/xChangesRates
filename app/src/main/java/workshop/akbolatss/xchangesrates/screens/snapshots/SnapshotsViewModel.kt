package workshop.akbolatss.xchangesrates.screens.snapshots

import androidx.lifecycle.MutableLiveData
import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.domain.usecase.FindAllSnapshotsUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.UpdateSingleSnapshotUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.UpdateSnapshotListUseCase
import workshop.akbolatss.xchangesrates.model.response.ChartData

class SnapshotsViewModel(
        private val findAllSnapshotsUseCase: FindAllSnapshotsUseCase,
        private val updateSingleSnapshotUseCase: UpdateSingleSnapshotUseCase,
        private val updateSnapshotListUseCase: UpdateSnapshotListUseCase
) : BaseViewModel() {

    val snapshots = MutableLiveData<List<ChartData>>()

    init {
        loadSnapshots()
    }

    fun loadSnapshots() {
        launchOperation(operation = { scope ->
            findAllSnapshotsUseCase(scope, FindAllSnapshotsUseCase.Params())
        }, success = {
            snapshots.value = it
        })
    }

    fun updateSingle(itemId: Long, position: Int) {
        launchOperation(operation = { scope ->
            updateSingleSnapshotUseCase(scope, UpdateSingleSnapshotUseCase.Params(itemId))
        }, success = {
            loadSnapshots()
        })
    }

    fun updateAll() {
        launchOperation(operation = { scope ->
            updateSnapshotListUseCase(scope, UpdateSnapshotListUseCase.Params())
        }, success = {
            loadSnapshots()
        })
    }
}

