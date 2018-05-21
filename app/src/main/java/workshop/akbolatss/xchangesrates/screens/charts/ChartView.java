package workshop.akbolatss.xchangesrates.screens.charts;

import workshop.akbolatss.xchangesrates.base.BaseView;
import workshop.akbolatss.xchangesrates.base.LoadingView;
import workshop.akbolatss.xchangesrates.model.response.ChartResponseData;

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

public interface ChartView extends BaseView, LoadingView {

    void onLoadLineChart(ChartResponseData chartData);
}
