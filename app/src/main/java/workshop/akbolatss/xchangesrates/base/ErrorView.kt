package workshop.akbolatss.xchangesrates.base

/**
 * Author: Akbolat Sadvakassov
 * Date: 27.12.2017
 */

interface ErrorView {

    fun onUnknownError(errorMessage: String)

    fun onTimeout()

    fun onNetworkError()
}
