package workshop.akbolatss.xchangesrates.utils.binding

import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import workshop.akbolatss.xchangesrates.databinding.ItemSnapshotBinding
import workshop.akbolatss.xchangesrates.domain.model.PriceByTime
import workshop.akbolatss.xchangesrates.utils.chart.DateXAxisFormatter
import workshop.akbolatss.xchangesrates.utils.chart.SnapshotLineDataSet

@BindingAdapter("entries")
fun LineChart.bindData(entryList: List<PriceByTime>?) {
    entryList?.let { safeEntries ->
        val binding = DataBindingUtil.getBinding<ItemSnapshotBinding>(rootView)
        binding?.lifecycleOwner?.let { lifecycleOwner ->
            val context = binding.root.context
            lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val entries = safeEntries.mapIndexed { index, priceByTime ->
                    Entry(
                        index.toFloat(),
                        priceByTime.price.toFloat()
                    )
                }
                xAxis.valueFormatter = DateXAxisFormatter(safeEntries)

                if (lineData.dataSetCount == 0) {
                    val dataSet = SnapshotLineDataSet(entries, null, context)
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
