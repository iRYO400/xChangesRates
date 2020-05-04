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
import workshop.akbolatss.xchangesrates.presentation.base.Error
import workshop.akbolatss.xchangesrates.presentation.base.Loading
import workshop.akbolatss.xchangesrates.presentation.base.Success
import workshop.akbolatss.xchangesrates.presentation.base.ViewState

abstract class BaseViewModel : ViewModel() {

    val loadingState: LiveData<LoadingState>
        get() = _loadingState
    val viewState: LiveData<ViewState>
        get() = _viewState
    val failure: LiveData<Failure>
        get() = _failure

    private val _loadingState = MutableLiveData<LoadingState>()

    private val _failure = MutableLiveData<Failure>()

    private val _viewState = MutableLiveData<ViewState>()

    private val handleLoadingState: (LoadingState) -> Unit = {
        _loadingState.value = it
    }

    private val handleViewState: (ViewState) -> Unit = {
        _viewState.value = it
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
            loading.invoke(LoadingState.Success)
        }
    }

    protected fun <T> executeUseCase(
        viewState: (ViewState) -> Unit = handleViewState,
        action: suspend (CoroutineScope) -> Either<Failure, T>
    ): Job {
        return viewModelScope.launch {
            viewState.invoke(Loading)
            action(this).fold(fnL = {
                viewState.invoke(Error(it))
            }, fnR = {
                viewState.invoke(Success(it))
            })
        }
    }

}
