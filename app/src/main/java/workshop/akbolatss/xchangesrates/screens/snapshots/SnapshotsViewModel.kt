package workshop.akbolatss.xchangesrates.screens.snapshots

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.domain.usecase.FindAllSnapshotsUseCase

class SnapshotsViewModel(
    findAllSnapshotsUseCase: FindAllSnapshotsUseCase
) : BaseViewModel() {

    val snapshots = findAllSnapshotsUseCase(FindAllSnapshotsUseCase.Params())
        .asLiveData(viewModelScope.coroutineContext)

    fun updateSingle(itemId: Long, position: Int) {

    }

    fun updateAll() {

    }
}

