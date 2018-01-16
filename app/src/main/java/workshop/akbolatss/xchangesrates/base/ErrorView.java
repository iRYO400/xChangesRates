package workshop.akbolatss.xchangesrates.base;

/**
 * Author: Akbolat Sadvakassov
 * Date: 27.12.2017
 */

public interface ErrorView {

    public void onUnknownError(String errorMessage);

    public void onTimeout();

    public void onNetworkError();
}
