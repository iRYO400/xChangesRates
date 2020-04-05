package workshop.akbolatss.xchangesrates.screens.snapshots.dialog.options

import androidx.lifecycle.MutableLiveData
import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.domain.usecase.FindSnapshotByIdUseCase
import workshop.akbolatss.xchangesrates.model.response.ChartData

class SnapshotOptionsViewModel(
        private val findSnapshotByIdUseCase: FindSnapshotByIdUseCase,
        private val itemId: Long?
) : BaseViewModel() {

    val snapshot = MutableLiveData<ChartData>()

    init {
        if (itemId == null)
            throw RuntimeException("itemId is null")
        loadSnapshot()
    }

    private fun loadSnapshot() {
        itemId?.let {
            launchOperation(operation = { scope ->
                findSnapshotByIdUseCase(scope, FindSnapshotByIdUseCase.Params(itemId))
            }, success = {
                snapshot.value = it
            })
        }
    }

}
