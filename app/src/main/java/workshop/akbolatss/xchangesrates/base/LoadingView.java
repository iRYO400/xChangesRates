package workshop.akbolatss.xchangesrates.base;

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

public interface LoadingView {

    void onShowLoading();

    void onHideLoading();

    void onNoContent(boolean isEmpty);
}
