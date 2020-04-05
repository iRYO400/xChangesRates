package workshop.akbolatss.xchangesrates.presentation.root

import androidx.lifecycle.MutableLiveData
import kz.jgroup.pos.util.Event
import workshop.akbolatss.xchangesrates.base.BaseViewModel

class RootViewModel : BaseViewModel() {

    val screenState = MutableLiveData<Event<ScreenState>>()

    fun showList() {
        if (screenState.value?.peekContent() != ScreenState.SNAPSHOTS)
            screenState.value = Event(ScreenState.SNAPSHOTS)
    }

    fun showCharts() {
        if (screenState.value?.peekContent() != ScreenState.CHART)
            screenState.value = Event(ScreenState.CHART)
    }

}
