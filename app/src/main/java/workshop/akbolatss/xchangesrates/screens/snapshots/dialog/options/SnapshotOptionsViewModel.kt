package workshop.akbolatss.xchangesrates.screens.snapshots.dialog.options

import androidx.lifecycle.MutableLiveData
import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.domain.model.Snapshot

class SnapshotOptionsViewModel(
    private val itemId: Long?
) : BaseViewModel() {

    val snapshot = MutableLiveData<Snapshot>()

    init {
        if (itemId == null)
            throw RuntimeException("itemId is null")
        loadSnapshot()
    }

    private fun loadSnapshot() {
    }

}
