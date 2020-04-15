package workshop.akbolatss.xchangesrates.screens.snapshots

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.domain.usecase.FindAllSnapshotsUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.ToggleNotificationUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.UpdateSnapshotUseCase
import workshop.akbolatss.xchangesrates.presentation.base.ViewState

class SnapshotsViewModel(
    findAllSnapshotsUseCase: FindAllSnapshotsUseCase,
    private val updateSnapshotUseCase: UpdateSnapshotUseCase,
    private val toggleNotificationUseCase: ToggleNotificationUseCase
) : BaseViewModel() {

    val snapshots = findAllSnapshotsUseCase(FindAllSnapshotsUseCase.Params())
        .asLiveData(viewModelScope.coroutineContext)

    val updatingItemViewState = MutableLiveData<Pair<ViewState, Int>>()

    fun updateSingle(itemId: Long, position: Int) {
        executeUseCase(
            viewState = { viewState ->
                updatingItemViewState.value = Pair(viewState, position)
            })
        { scope ->
            updateSnapshotUseCase(scope, UpdateSnapshotUseCase.Params(itemId))
        }
    }

    fun updateAll() {

    }

    fun toggleNotification(itemId: Long, pos: Int) {
        executeUseCase(

        ) { scope ->
            toggleNotificationUseCase(scope, ToggleNotificationUseCase.Params(itemId))
        }

    }
}

