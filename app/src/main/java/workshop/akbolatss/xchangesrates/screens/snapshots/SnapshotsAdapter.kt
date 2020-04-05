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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.BaseRVA
import workshop.akbolatss.xchangesrates.base.DataBoundViewHolder
import workshop.akbolatss.xchangesrates.databinding.RvSnapshotBinding
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.model.response.ChartInfo
import workshop.akbolatss.xchangesrates.utils.UtilityMethods.convertTime

/**
 * Author: Akbolat Sadvakassov
 * Date: 18.12.2017
 */

class SnapshotsAdapter(private val itemClickListener: (Long, Int) -> Unit,
                       private val notificationToggleClickListener: (ChartData, Int) -> Unit,
                       private val openOptionsClickListener: (Long, Int) -> Unit) : BaseRVA<ChartData>(DIFF_CALLBACK) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        val viewBinding = DataBindingUtil.inflate<RvSnapshotBinding>(
                LayoutInflater.from(parent.context),
                R.layout.rv_snapshot,
                parent, false
        )
        initChartView(viewBinding)
        return viewBinding
    }

    private fun initChartView(viewBinding: RvSnapshotBinding) = with(viewBinding) {
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
    override fun bind(holder: DataBoundViewHolder, item: ChartData) {
        when (holder.binding) {
            is RvSnapshotBinding -> with(holder.binding) {
                tvSnapName.text = item.coin + "/" + item.currency
                tvExchanger.text = item.exchange

                if (item.isNotificationEnabled) {
                    imgNotifyState.setImageResource(R.drawable.ic_round_notifications_active_24)
                } else {
                    imgNotifyState.setImageResource(R.drawable.ic_round_notifications_none_24)
                }

                bindInfo(this, item.info)

                lifecycleOwner?.lifecycleScope?.launchWhenResumed {
                    withContext(Dispatchers.IO) {
                        val barEntries = item.chart.map { chartItem ->
                            BarEntry(chartItem.timestamp!!.toFloat(),
                                    chartItem.price!!.toFloat())
                        }

                        val dataSet = LineDataSet(barEntries, "Entries").apply {
                            setDrawCircles(false)
                            mode = LineDataSet.Mode.CUBIC_BEZIER
                            cubicIntensity = 0.4f
                            lineWidth = 1f
                            color = ContextCompat.getColor(root.context, R.color.colorAccent)
                            setDrawFilled(true)
                            fillDrawable = ContextCompat.getDrawable(root.context, R.drawable.bg_chart_round)
                        }

                        val lineData = LineData(dataSet).apply {
                            setDrawValues(false)
                            isHighlightEnabled = false
                        }
                        lineChart.clear()
                        lineChart.data = lineData
                    }
                    lineChart.notifyDataSetChanged()
                }
                setListeners(this, item, holder.layoutPosition)
            }
        }
    }

    private fun bindInfo(viewBinding: RvSnapshotBinding, chartInfo: ChartInfo) =
            with(viewBinding) {
                tvCurrRate.text = chartInfo.last
                tvHighRate.text = chartInfo.high
                tvLowRate.text = chartInfo.low
                tvTime.text = convertTime(chartInfo.timestamp!! * 1000)
            }

    private fun setListeners(viewBinding: RvSnapshotBinding, item: ChartData, layoutPosition: Int) =
            with(viewBinding) {
                snapshotView.setOnClickListener {
                    itemClickListener(item.id, layoutPosition)
                }

                flNotify.setOnClickListener {
                    notificationToggleClickListener(item, layoutPosition)
                }

                flOptions.setOnClickListener {
                    openOptionsClickListener(item.id, layoutPosition)
                }
            }

}

private val DIFF_CALLBACK: DiffUtil.ItemCallback<ChartData> =
        object : DiffUtil.ItemCallback<ChartData>() {

            override fun areItemsTheSame(
                    oldItem: ChartData,
                    newItem: ChartData
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                    oldItem: ChartData,
                    newItem: ChartData
            ): Boolean {
                return oldItem.exchange == newItem.exchange &&
                        oldItem.coin == newItem.coin &&
                        oldItem.currency == newItem.currency
            }

        }
