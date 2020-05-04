package workshop.akbolatss.xchangesrates.utils.binding

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import workshop.akbolatss.xchangesrates.domain.model.ChartDot
import workshop.akbolatss.xchangesrates.utils.chart.DateXAxisFormatter
import workshop.akbolatss.xchangesrates.utils.chart.SnapshotListLineDataSet

@BindingAdapter("entries", "lifecycleOwner")
fun LineChart.bindData(entryList: List<ChartDot>?, lifecycleOwner: LifecycleOwner?) {
    entryList?.let { safeEntries ->
        lifecycleOwner?.let { lifecycleOwner ->
            lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val entries = safeEntries.mapIndexed { index, priceByTime ->
                    Entry(
                        index.toFloat(),
                        priceByTime.price.toFloat()
                    )
                }
                xAxis.valueFormatter = DateXAxisFormatter(safeEntries)

                if (lineData.dataSetCount == 0) {
                    val dataSet = SnapshotListLineDataSet(entries, null, context)
                    lineData.addDataSet(dataSet)
                } else {
                    val dataSet = lineData.getDataSetByIndex(0) as LineDataSet
                    dataSet.values = entries
                    dataSet.notifyDataSetChanged()
                }
                lineData.notifyDataChanged()
                notifyDataSetChanged()

                withContext(Dispatchers.Main) {
                    invalidate()
                }
            }
        }
    }
}
