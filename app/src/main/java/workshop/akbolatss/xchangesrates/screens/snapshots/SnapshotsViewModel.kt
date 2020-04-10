package workshop.akbolatss.xchangesrates.screens.snapshots

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.domain.usecase.FindAllSnapshotsUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.UpdateSingleSnapshotUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.UpdateSnapshotListUseCase

class SnapshotsViewModel(
    private val findAllSnapshotsUseCase: FindAllSnapshotsUseCase,
    private val updateSingleSnapshotUseCase: UpdateSingleSnapshotUseCase,
    private val updateSnapshotListUseCase: UpdateSnapshotListUseCase
) : BaseViewModel() {

    val snapshots = findAllSnapshotsUseCase(FindAllSnapshotsUseCase.Params())
        .asLiveData(viewModelScope.coroutineContext)

    fun updateSingle(itemId: Long, position: Int) {
        launchOperation(operation = { scope ->
            updateSingleSnapshotUseCase(scope, UpdateSingleSnapshotUseCase.Params(itemId))
        }, success = {
        })
    }

    fun updateAll() {
        launchOperation(operation = { scope ->
            updateSnapshotListUseCase(scope, UpdateSnapshotListUseCase.Params())
        }, success = {
        })
    }
}

