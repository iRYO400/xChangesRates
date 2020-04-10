package workshop.akbolatss.xchangesrates.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import timber.log.Timber
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.base.resource.LoadingState

abstract class BaseViewModel : ViewModel() {

    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    val failure: LiveData<Failure>
        get() = _failure

    private val _loadingState = MutableLiveData<LoadingState>()
    private val _failure = MutableLiveData<Failure>()

    private val handleLoadingState: (LoadingState) -> Unit = {
        _loadingState.value = it
    }

    private val handleFailure: (Failure) -> Unit = {
        Timber.e("ViewModel failure: ${it.errorMessage} by $it")
        _failure.value = it
    }

    fun <T> launchOperation(
        operation: suspend (CoroutineScope) -> Either<Failure, T>,
        loading: (LoadingState) -> Unit = handleLoadingState,
        success: (T) -> Unit,
        failure: (Failure) -> Unit = handleFailure
    ): Job {
        return viewModelScope.launch {
            loading.invoke(LoadingState.Loading)
            withContext(Dispatchers.IO) {
                operation(this)
            }.fold(failure, success)
            loading.invoke(LoadingState.Idle)
        }
    }

    protected fun executeUseCase(
        loading: (LoadingState) -> Unit = handleLoadingState,
        action: suspend (CoroutineScope) -> Unit
    ): Job {
        return viewModelScope.launch {
            loading.invoke(LoadingState.Loading)
            action(this)
            loading.invoke(LoadingState.Idle)
        }
    }

}
