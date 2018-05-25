package workshop.akbolatss.xchangesrates.base

import java.lang.ref.WeakReference

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

abstract class BasePresenter<V : BaseView> {

    private var mView: WeakReference<V>? = null

    val view: V
        get() = mView?.get()!!

    open fun onViewAttached(view: V?) {
        if (view == null) {
            throw NullPointerException("View must not be null")
        }

        if (this.mView != null) onViewDetached(this.mView!!.get())

        this.mView = WeakReference(view)
    }

    open fun onViewDetached(view: V?) {
        if (view == null) throw NullPointerException("Detached view must not be null")

        this.mView!!.clear()
    }
}
