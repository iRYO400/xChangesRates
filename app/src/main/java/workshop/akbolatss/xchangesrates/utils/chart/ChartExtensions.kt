package workshop.akbolatss.xchangesrates.utils.chart

import android.content.Context
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.utils.extension.getThemeColor

fun LineChart.setupChartInList() {
    isLogEnabled = true
    description.isEnabled = false
    legend.isEnabled = false
    setTouchEnabled(false)
    setViewPortOffsets(8f, 0f, 0f, 0f)

    with(xAxis) {
        isEnabled = false
    }
    with(axisLeft) {
        isEnabled = false
    }
    with(axisRight) {
        isEnabled = false
    }
    val lineDataSet = SnapshotLineDataSet(emptyList(), null, context)
    data = LineData(lineDataSet).apply {
        setDrawValues(false)
        isHighlightEnabled = false
    }
}

fun LineChart.setupChart(context: Context) {
    isLogEnabled = false
    description.isEnabled = false
    legend.isEnabled = false
    setNoDataTextColor(context.getThemeColor(R.attr.colorError))
    setMaxVisibleValueCount(20)

    with(xAxis) {
        position = XAxis.XAxisPosition.BOTTOM
        labelCount = 3
        textColor = context.getThemeColor(R.attr.colorOnSecondary)
        setDrawGridLines(false)
    }

    with(axisLeft) {
        textColor = context.getThemeColor(R.attr.colorOnSecondary)
    }
    with(axisRight) {
        isEnabled = false
    }

    val lineDataSet = ChartFragmentLineDataSet(emptyList(), null, context)
    data = LineData(lineDataSet).apply {
        setValueTextSize(9f)
        isHighlightEnabled = false
    }
}
