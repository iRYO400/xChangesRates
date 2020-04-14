package workshop.akbolatss.xchangesrates.screens.snapshots

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.BaseRVA
import workshop.akbolatss.xchangesrates.base.DataBoundViewHolder
import workshop.akbolatss.xchangesrates.databinding.ItemSnapshotBinding
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.utils.UtilityMethods.convertTime
import workshop.akbolatss.xchangesrates.utils.extension.getThemeColor

class SnapshotsAdapter(
    private val itemClickListener: (Long, Int) -> Unit
) : BaseRVA<Snapshot>(DIFF_CALLBACK) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        val viewBinding = DataBindingUtil.inflate<ItemSnapshotBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_snapshot,
            parent, false
        )
        initChartView(viewBinding)
        return viewBinding
    }

    private fun initChartView(viewBinding: ItemSnapshotBinding) = with(viewBinding) {
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.setTouchEnabled(false)
        lineChart.setViewPortOffsets(8f, 0f, 0f, 0f)

        val xAxis = lineChart.xAxis
        xAxis.isEnabled = false

        val yAxis = lineChart.axisLeft
        yAxis.isEnabled = false

        val yAxis1 = lineChart.axisRight
        yAxis1.isEnabled = false
    }

    @SuppressLint("SetTextI18n")
    override fun bind(holder: DataBoundViewHolder, item: Snapshot) {
        when (holder.binding) {
            is ItemSnapshotBinding -> with(holder.binding) {
                tvSnapName.text = item.coin + "/" + item.currency
                tvExchanger.text = item.exchangerName
                tvCurrRate.text = item.rate.toPlainString()
                tvHighRate.text = item.high.toPlainString()
                tvLowRate.text = item.low.toPlainString()
                tvChange.text = "TODO"
                tvTime.text = convertTime(item.updateTime.time)

                lifecycleOwner?.lifecycleScope?.launch {
                    val barEntries = item.charts.map { chartItem ->
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
                        color = holder.binding.root.context.getThemeColor(R.attr.colorAccent)
                        setDrawFilled(true)
                        fillDrawable =
                            ContextCompat.getDrawable(root.context, R.drawable.bg_chart_round)
                    }

                    val lineData = LineData(dataSet).apply {
                        setDrawValues(false)
                        isHighlightEnabled = false
                    }
                    lineChart.clear()
                    lineChart.data = lineData
                    lineChart.notifyDataSetChanged()
                }
                setListeners(this, item, holder.layoutPosition)
            }
        }
    }

    private fun setListeners(
        viewBinding: ItemSnapshotBinding,
        item: Snapshot,
        layoutPosition: Int
    ) =
        with(viewBinding) {
            snapshotView.setOnClickListener {
                itemClickListener(item.id, layoutPosition)
            }
        }
}

private val DIFF_CALLBACK: DiffUtil.ItemCallback<Snapshot> =
    object : DiffUtil.ItemCallback<Snapshot>() {

        override fun areItemsTheSame(
            oldItem: Snapshot,
            newItem: Snapshot
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Snapshot,
            newItem: Snapshot
        ): Boolean {
            return oldItem.exchangerName == newItem.exchangerName &&
                    oldItem.coin == newItem.coin &&
                    oldItem.currency == newItem.currency &&
                    oldItem.rate == newItem.rate &&
                    oldItem.high == newItem.high &&
                    oldItem.low == newItem.low
        }

    }
