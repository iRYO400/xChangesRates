package workshop.akbolatss.xchangesrates.presentation.snapshots

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kz.jgroup.pos.util.Event
import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.base.resource.onSuccess
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.usecase.FindAllSnapshotsFlowUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.ToggleNotificationUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.UpdateSnapshotUseCase
import workshop.akbolatss.xchangesrates.presentation.base.ViewState

class SnapshotsViewModel(
    findAllSnapshotsUseCase: FindAllSnapshotsFlowUseCase,
    private val updateSnapshotUseCase: UpdateSnapshotUseCase,
    private val toggleNotificationUseCase: ToggleNotificationUseCase
) : BaseViewModel() {

    val snapshots = findAllSnapshotsUseCase(FindAllSnapshotsFlowUseCase.Params())
        .asLiveData(viewModelScope.coroutineContext)

    val updatingItemViewState = MutableLiveData<Pair<ViewState, Int>>()

    val snapshot2ToggleNotification = MutableLiveData<Event<Snapshot>>()

    val snapshotClickedTimes = MutableLiveData<Event<Int>>()

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
        snapshots.value?.let { snapshots ->
            snapshots.forEachIndexed { index, snapshot ->
                updateSingle(snapshot.id, index)
            }
        }
    }

    fun toggleNotification(itemId: Long) {
        executeUseCase { scope ->
            toggleNotificationUseCase(scope, ToggleNotificationUseCase.Params(itemId))
                .onSuccess { snapshot ->
                    snapshot2ToggleNotification.value = Event(snapshot)
                }
        }
    }

    fun updateSnapshotClickTimes() {
        var current = snapshotClickedTimes.value?.peekContent() ?: 0
        snapshotClickedTimes.value = Event(++current)
    }

}

