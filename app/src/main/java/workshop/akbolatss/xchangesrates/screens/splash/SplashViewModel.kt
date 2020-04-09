package workshop.akbolatss.xchangesrates.screens.splash

import androidx.lifecycle.MutableLiveData
import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.domain.usecase.DownloadExchangesUseCase

class SplashViewModel(
    private val downloadExchangesUseCase: DownloadExchangesUseCase
) : BaseViewModel() {

    val success = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()

    init {
        loadData()
    }

    private fun loadData() {
        launchOperation(operation = { scope ->
            downloadExchangesUseCase(scope, DownloadExchangesUseCase.Params())
        }, success = {
            success.value = true
        })
    }
}
