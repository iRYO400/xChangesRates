package workshop.akbolatss.xchangesrates.screens.snapshots

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.BaseRVA
import workshop.akbolatss.xchangesrates.base.DataBoundViewHolder
import workshop.akbolatss.xchangesrates.databinding.RvSnapshotBinding
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.utils.UtilityMethods.convertTime

class SnapshotsAdapter(
    private val itemClickListener: (Long, Int) -> Unit,
    private val notificationToggleClickListener: (Snapshot, Int) -> Unit,
    private val openOptionsClickListener: (Long, Int) -> Unit
) : BaseRVA<Snapshot>(DIFF_CALLBACK) {

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
    override fun bind(holder: DataBoundViewHolder, item: Snapshot) {
        when (holder.binding) {
            is RvSnapshotBinding -> with(holder.binding) {
                tvSnapName.text = item.coin + "/" + item.currency
                tvExchanger.text = item.exchangerName

//                if (item.isNotificationEnabled) {
//                    imgNotifyState.setImageResource(R.drawable.ic_round_notifications_active_24)
//                } else {
//                    imgNotifyState.setImageResource(R.drawable.ic_round_notifications_none_24)
//                }

                tvCurrRate.text = item.rate.toPlainString()
                tvHighRate.text = item.high.toPlainString()
                tvLowRate.text = item.low.toPlainString()
                tvTime.text = convertTime(item.updateTime.time * 1000)

//                lifecycleOwner?.lifecycleScope?.launchWhenResumed {
//                    withContext(Dispatchers.IO) {
//                        val barEntries = item.chart.map { chartItem ->
//                            BarEntry(
//                                chartItem.timestamp!!.toFloat(),
//                                chartItem.price!!.toFloat()
//                            )
//                        }
//
//                        val dataSet = LineDataSet(barEntries, "Entries").apply {
//                            setDrawCircles(false)
//                            mode = LineDataSet.Mode.CUBIC_BEZIER
//                            cubicIntensity = 0.4f
//                            lineWidth = 1f
//                            color = ContextCompat.getColor(root.context, R.color.colorAccent)
//                            setDrawFilled(true)
//                            fillDrawable =
//                                ContextCompat.getDrawable(root.context, R.drawable.bg_chart_round)
//                        }
//
//                        val lineData = LineData(dataSet).apply {
//                            setDrawValues(false)
//                            isHighlightEnabled = false
//                        }
//                        lineChart.clear()
//                        lineChart.data = lineData
//                    }
//                    lineChart.notifyDataSetChanged()
//                }
                setListeners(this, item, holder.layoutPosition)
            }
        }
    }

    private fun setListeners(viewBinding: RvSnapshotBinding, item: Snapshot, layoutPosition: Int) =
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
