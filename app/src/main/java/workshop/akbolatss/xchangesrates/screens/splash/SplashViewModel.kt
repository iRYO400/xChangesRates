package workshop.akbolatss.xchangesrates.screens.splash

import androidx.lifecycle.MutableLiveData
import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.domain.usecase.UpdateDataUseCase

class SplashViewModel(
    private val updateDataUseCase: UpdateDataUseCase
) : BaseViewModel() {

    val success = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()

    init {
        loadData()
    }

    private fun loadData() {
        launchOperation(operation = { scope ->
            updateDataUseCase(scope, UpdateDataUseCase.Params())
        }, success = {
            success.value = true
        })
    }
}
