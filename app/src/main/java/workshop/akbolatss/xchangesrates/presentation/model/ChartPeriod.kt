package workshop.akbolatss.xchangesrates.presentation.model

import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.utils.Constants

data class ChartPeriod(
    val code: String,
    val nameRes: Int,
    val isSelected: Boolean
)

val defaultChartPeriodList = listOf(
    ChartPeriod(Constants.MINUTES_10, R.string.tv10min, false),
    defaultChartPeriod(),
    ChartPeriod(Constants.HOUR_3, R.string.tv3h, false),
    ChartPeriod(Constants.HOUR_12, R.string.tv12h, false),
    ChartPeriod(Constants.HOUR_24, R.string.tv24h, false),
    ChartPeriod(Constants.WEEK, R.string.tv1w, false),
    ChartPeriod(Constants.MONTH, R.string.tv1m, false),
    ChartPeriod(Constants.MONTH_3, R.string.tv3m, false),
    ChartPeriod(Constants.MONTH_6, R.string.tv6m, false),
    ChartPeriod(Constants.YEAR_1, R.string.tv1y, false),
    ChartPeriod(Constants.YEAR_2, R.string.tv2y, false),
    ChartPeriod(Constants.YEAR_5, R.string.tv5y, false)
)

fun defaultChartPeriod() = ChartPeriod(Constants.HOUR_1, R.string.tv1h, true)

