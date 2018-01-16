package workshop.akbolatss.xchangesrates.base;

import java.lang.ref.WeakReference;

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

public abstract class BasePresenter<V extends BaseView> {

    private WeakReference<V> mView;

    public V getView() {
        return mView.get();
    }

    public void onViewAttached(V view) {
        if (view == null) {
            throw new NullPointerException("View must not be null");
        }

        if(this.mView != null) onViewDetached(this.mView.get());

        this.mView = new WeakReference<V>(view);
    }

    public void onViewDetached(V view) {
        if (view == null) throw new NullPointerException("Detached view must not be null");

        this.mView.clear();
    }
}
