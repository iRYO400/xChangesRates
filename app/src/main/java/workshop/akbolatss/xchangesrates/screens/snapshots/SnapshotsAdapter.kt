package workshop.akbolatss.xchangesrates.screens.snapshots

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.rv_snapshot.view.*
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.model.response.ChartInfo
import workshop.akbolatss.xchangesrates.model.response.ChartItem
import workshop.akbolatss.xchangesrates.utils.UtilityMethods.convertTime
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Author: Akbolat Sadvakassov
 * Date: 18.12.2017
 */

class SnapshotsAdapter(private val mListener: OnSnapshotListener) : RecyclerView.Adapter<SnapshotsAdapter.SnapshotsVH>() {

    val mList: MutableList<ChartData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotsVH {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_snapshot, parent, false)
        return SnapshotsVH(view)
    }

    override fun onBindViewHolder(holder: SnapshotsVH, position: Int) {
        holder.bind(mList[position], mListener)
    }

    /**
     * Add snapshots
     */
    fun onAddItems(modelList: List<ChartData>) {
//        val diffResult = DiffUtil.calculateDiff(SnapshotDiffUtillCallback(mList, modelList))
        mList.clear()
        mList.addAll(modelList)
        notifyDataSetChanged()
//        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * Update snapshot
     */
    fun onUpdateSnapshot(chartData: ChartData, pos: Int) {
        mList[pos] = chartData
        notifyItemChanged(pos)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    interface OnSnapshotListener {

        fun onUpdateItem(chartData: ChartData, pos: Int)

        fun onOpenOptions(chartId: Long, pos: Int)

        fun enableNotification(chartData: ChartData, pos: Int)
    }

    inner class SnapshotsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            onInitChart()
        }

        fun bind(chartData: ChartData, listener: OnSnapshotListener) {
            val s = chartData.coin + "/" + chartData.currency
            itemView.tvSnapName.text = s
            itemView.tvExchanger.text = chartData.exchange

            if (chartData.isNotificationEnabled) {
                itemView.imgNotifyState.setImageResource(R.drawable.ic_round_notifications_active_24)
            } else {
                itemView.imgNotifyState.setImageResource(R.drawable.ic_round_notifications_none_24)
            }

            itemView.snapshotView.setOnClickListener {
                listener.onUpdateItem(chartData, adapterPosition)
            }

            itemView.snapshotView.setOnLongClickListener {
                listener.onOpenOptions(chartData.id, adapterPosition)
                true
            }

            itemView.flNotify.setOnClickListener {
                listener.enableNotification(chartData, adapterPosition)
            }

            itemView.flOptions.setOnClickListener {
                listener.onOpenOptions(chartData.id, adapterPosition)
            }

            bindInfo(chartData.info)
            bindChart(chartData.chart)
        }

        private fun bindInfo(chartInfo: ChartInfo) {
            itemView.tvCurrRate.text = chartInfo.last
            itemView.tvHighRate.text = chartInfo.high
            itemView.tvLowRate.text = chartInfo.low
            itemView.tvTime.text = convertTime(chartInfo.timestamp!! * 1000)
        }

        private fun onInitChart() {
            itemView.lineChart.description.isEnabled = false
            itemView.lineChart.legend.isEnabled = false
            itemView.lineChart.setTouchEnabled(false)
            itemView.lineChart.setViewPortOffsets(8f, 0f, 0f, 0f)

            val xAxis = itemView.lineChart.xAxis
            xAxis.isEnabled = false

            val yAxis = itemView.lineChart.axisLeft
            yAxis.isEnabled = false

            val yAxis1 = itemView.lineChart.axisRight
            yAxis1.isEnabled = false
        }

        @SuppressLint("CheckResult")
        private fun bindChart(chartsList: List<ChartItem>) {
            rxCalculate(chartsList)
                    .delay(1000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ lineData ->
                        itemView.lineChart.clear()
                        itemView.lineChart.data = lineData
                        itemView.lineChart.notifyDataSetChanged()
                    }, {

                    })
        }

        private fun rxCalculate(chartsList: List<ChartItem>): Single<LineData> {
            return Observable.fromIterable(chartsList)
                    .map { chartItem ->
                        BarEntry(chartItem.timestamp!!.toFloat(),
                                chartItem.price!!.toFloat())
                    }
                    .toList()
                    .map {
                        val dataSet = LineDataSet(it as List<Entry>, "")
                        dataSet.setDrawCircles(false)
                        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                        dataSet.cubicIntensity = 0.4f
                        dataSet.lineWidth = 1f
                        dataSet.color = ContextCompat.getColor(itemView.context, R.color.colorAccent)
                        dataSet.setDrawFilled(true)
                        dataSet.fillDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.bg_chart_round)
                        dataSet
                    }
                    .map { dataSet ->
                        val lineData = LineData(dataSet)
                        lineData.setDrawValues(false)
                        lineData.isHighlightEnabled = false
                        lineData
                    }
        }
    }
}
