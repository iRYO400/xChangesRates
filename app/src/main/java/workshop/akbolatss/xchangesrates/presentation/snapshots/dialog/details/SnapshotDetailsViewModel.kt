package workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.details

import androidx.lifecycle.MutableLiveData
import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.base.resource.onFailure
import workshop.akbolatss.xchangesrates.base.resource.onSuccess
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.usecase.FindSnapshotByIdUseCase
import workshop.akbolatss.xchangesrates.presentation.base.Error
import workshop.akbolatss.xchangesrates.presentation.base.ViewState

class SnapshotDetailsViewModel(
    private val findSnapshotByIdUseCase: FindSnapshotByIdUseCase,
    private val snapshotId: Long
) : BaseViewModel() {

    val snapshot = MutableLiveData<Snapshot>()
    val snapshotError = MutableLiveData<ViewState>()

    init {
        loadSnapshot()
    }

    private fun loadSnapshot() {
        executeUseCase { scope ->
            findSnapshotByIdUseCase(scope, FindSnapshotByIdUseCase.Params(snapshotId))
                .onSuccess { snapshot ->
                    this.snapshot.value = snapshot
                }
                .onFailure { failure ->
                    snapshotError.value = Error(failure)
                }
        }
    }
}
