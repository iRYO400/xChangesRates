package workshop.akbolatss.xchangesrates.presentation.splash

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.base.resource.onFailure
import workshop.akbolatss.xchangesrates.base.resource.onSuccess
import workshop.akbolatss.xchangesrates.domain.usecase.DownloadExchangesUseCase

class SplashViewModel(
    private val downloadExchangesUseCase: DownloadExchangesUseCase
) : BaseViewModel() {

    val exchangersUpdated = MutableLiveData<Boolean>()
    val exchangersUpdatedError = MutableLiveData<Boolean>()
    val exitApp = MutableLiveData<Boolean>()

    init {
        loadData()
    }

    private fun loadData() {
        executeUseCase { scope ->
            downloadExchangesUseCase(scope, DownloadExchangesUseCase.Params())
                .onSuccess {
                    exchangersUpdated.value = true
                }
                .onFailure {
                    exchangersUpdatedError.value = true
                    delay(2500)
                    exitApp.value = true
                }
        }
    }
}
