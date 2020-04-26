package workshop.akbolatss.xchangesrates.utils.chart

import android.content.Context
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.utils.extension.getThemeColor

fun LineChart.setupLineChartInSnapshotList() {
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
    val lineDataSet = SnapshotListLineDataSet(emptyList(), null, context)
    data = LineData(lineDataSet).apply {
        setDrawValues(false)
        isHighlightEnabled = false
    }
}

fun LineChart.setupLineChartInChartOverview(context: Context) {
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

fun LineChart.setupLineChartInSnapshotDetails(
    context: Context,
    onEntryClickListener: (Entry) -> Unit
) {
    description.isEnabled = false
    legend.isEnabled = false
    setDrawGridBackground(false)
    setMaxVisibleValueCount(10)
    minOffset = 0f

    setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
        override fun onNothingSelected() = Unit

        override fun onValueSelected(e: Entry?, h: Highlight?) {
            e?.let(onEntryClickListener)
        }
    })

    setNoDataTextColor(context.getThemeColor(R.attr.colorError))

    with(xAxis) {
        isEnabled = false
    }

    with(axisLeft) {
        textColor = context.getThemeColor(R.attr.colorOnSecondary)
        labelCount = 6
        setDrawGridLines(true)
        setDrawAxisLine(false)
    }
    with(axisRight) {
        isEnabled = false
    }

    val lineDataSet = SnapshotDetailsLineDataSet(emptyList(), null, context)
    data = LineData(lineDataSet).apply {
        setValueTextSize(9f)
        isHighlightEnabled = true
    }
}
