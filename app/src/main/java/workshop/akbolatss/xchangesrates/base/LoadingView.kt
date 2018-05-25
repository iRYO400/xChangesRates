package workshop.akbolatss.xchangesrates.base

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

interface LoadingView {

    fun onShowLoading()

    fun onHideLoading()

    fun onNoContent(isEmpty: Boolean)
}
