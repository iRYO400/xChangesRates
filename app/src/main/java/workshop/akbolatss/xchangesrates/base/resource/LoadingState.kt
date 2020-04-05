package workshop.akbolatss.xchangesrates.base.resource

sealed class LoadingState {
    object Success : LoadingState()
    object Loading : LoadingState()
}
