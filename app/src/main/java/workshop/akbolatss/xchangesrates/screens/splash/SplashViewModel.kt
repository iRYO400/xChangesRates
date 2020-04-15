package workshop.akbolatss.xchangesrates.screens.splash

import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.domain.usecase.DownloadExchangesUseCase

class SplashViewModel(
    private val downloadExchangesUseCase: DownloadExchangesUseCase
) : BaseViewModel() {

    init {
        loadData()
    }

    private fun loadData() {
        executeUseCase { scope ->
            downloadExchangesUseCase(scope, DownloadExchangesUseCase.Params())
        }
    }
}
