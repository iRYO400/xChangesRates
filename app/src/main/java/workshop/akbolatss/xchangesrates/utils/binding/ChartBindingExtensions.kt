package workshop.akbolatss.xchangesrates.utils.binding

import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.databinding.ItemSnapshotBinding
import workshop.akbolatss.xchangesrates.domain.model.PriceByTime
import workshop.akbolatss.xchangesrates.utils.extension.getThemeColor

fun LineChart.setupChartInList() {
    description.isEnabled = false
    legend.isEnabled = false
    setTouchEnabled(false)
    setViewPortOffsets(8f, 0f, 0f, 0f)

    val xAxis = xAxis
    xAxis.isEnabled = false

    val yAxis = axisLeft
    yAxis.isEnabled = false

    val yAxis1 = axisRight
    yAxis1.isEnabled = false
}

@BindingAdapter("entries")
fun LineChart.bindData(entries: List<PriceByTime>?) {
    val safeEntries = entries ?: emptyList()
    val binding = DataBindingUtil.getBinding<ItemSnapshotBinding>(rootView)
    binding?.lifecycleOwner?.let { lifecycleOwner ->
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val barEntries = safeEntries.map { chartItem ->
                BarEntry(
                    chartItem.timestamp.toFloat(),
                    chartItem.price.toFloat()
                )
            }

            val dataSet = LineDataSet(barEntries, "Entries").apply {
                setDrawCircles(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                cubicIntensity = 0.4f
                lineWidth = 1f
                color = binding.root.context.getThemeColor(R.attr.colorAccent)
                setDrawFilled(true)
                fillDrawable =
                    ContextCompat.getDrawable(binding.root.context, R.drawable.bg_chart_round)
            }

            val lineData = LineData(dataSet).apply {
                setDrawValues(false)
                isHighlightEnabled = false
            }
            clear()
            data = lineData
            withContext(Dispatchers.Main) {
                notifyDataSetChanged()
            }
        }
    }
}
