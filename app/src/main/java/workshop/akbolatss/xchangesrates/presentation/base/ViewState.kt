package workshop.akbolatss.xchangesrates.presentation.base

import workshop.akbolatss.xchangesrates.base.resource.Failure

sealed class ViewState
class Success<out T>(val data: T) : ViewState()
class Error(val failure: Failure) : ViewState()
object Loading : ViewState()
class NoInternetState<T : Any> : ViewState()


