package workshop.akbolatss.xchangesrates.screens.snapshots

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.rv_snapshot.view.*
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.model.dao.Snapshot
import workshop.akbolatss.xchangesrates.model.dao.SnapshotChart
import workshop.akbolatss.xchangesrates.model.dao.SnapshotInfo
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Author: Akbolat Sadvakassov
 * Date: 18.12.2017
 */

class SnapshotsAdapter(private val mListener: onSnapshotClickListener) : RecyclerView.Adapter<SnapshotsAdapter.SnapshotsVH>() {

    private val mSnapshotModels: MutableList<Snapshot>?

    val snapshotModels: List<Snapshot>?
        get() = mSnapshotModels

    init {
        this.mSnapshotModels = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotsVH {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_snapshot, parent, false)
        return SnapshotsVH(view)
    }

    override fun onBindViewHolder(holder: SnapshotsVH, position: Int) {
        holder.bind(mSnapshotModels!![position], mListener)
    }

    fun onAddItems(modelList: List<Snapshot>?) {
        if (modelList != null) {
            mSnapshotModels!!.clear()
            mSnapshotModels.addAll(modelList)
            notifyDataSetChanged()
        }
    }

    fun onUpdateSnapshot(data: Snapshot, pos: Int) {
        mSnapshotModels!![pos] = data
        notifyItemChanged(pos)
    }

    fun onUpdateNotifyState(isActive: Boolean, timing: String, pos: Int) {
        val data = mSnapshotModels!![pos]
        data.isActiveForGlobal = isActive
        data.timing = timing
        mSnapshotModels[pos] = data
        notifyItemChanged(pos)
    }

    fun onUpdateInfo(info: SnapshotInfo, pos: Int) {
        mSnapshotModels!![pos].info = info
        notifyItemChanged(pos)
    }

    fun onRemoveSnap(pos: Int) {
        mSnapshotModels!!.removeAt(pos)
        notifyItemRemoved(pos)
    }

    override fun getItemCount(): Int {
        return mSnapshotModels?.size ?: 0
    }

    interface onSnapshotClickListener {

        fun onUpdateItem(model: Snapshot, pos: Int)

        fun onGetInfo(key: Long, pos: Int)

        fun onOpenOptions(chartId: Long, isActive: Boolean, timing: String, pos: Int)
    }

    inner class SnapshotsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            onInitChart()
        }

        fun bind(model: Snapshot, listener: onSnapshotClickListener) {
            val s = model.coin + "/" + model.currency
            itemView.tvSnapName.text = s
            itemView.tvExchanger.text = model.exchange

            if (model.isActiveForGlobal!!) {
                itemView.imgNotifyState.setImageResource(R.drawable.ic_notifications_active_24dp)
            } else {
                itemView.imgNotifyState.setImageResource(R.drawable.ic_notifications_inactive24dp)
            }

            itemView.frameLayout.setOnClickListener(View.OnClickListener {
                itemView.frameLayout.isEnabled = false
                itemView.frameLayout.isClickable = false
                itemView.progressBar.visibility = View.VISIBLE
                itemView.tvDate.visibility = View.INVISIBLE
                itemView.tvTime.visibility = View.INVISIBLE
                listener.onUpdateItem(model, adapterPosition)
            })

            itemView.frameLayout.setOnLongClickListener(View.OnLongClickListener {
                listener.onOpenOptions(model.id!!, model.isActiveForGlobal!!, model.timing, adapterPosition)
                true
            })


            if (!model.isInfoNull) {
                bindInfo(model.info)
            } else {
                listener.onGetInfo(model.id!!, adapterPosition)
            }

            bindChart(model.charts)
        }

        fun bindInfo(dataInfo: SnapshotInfo) {
            itemView.tvCurrRate.text = dataInfo.last
            itemView.tvHighRate.text = dataInfo.high
            itemView.tvLowRate.text = dataInfo.low
            val timestamp = dataInfo.updated.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            itemView.tvDate.text = timestamp[0]
            itemView.tvTime.text = timestamp[1]
        }

        private fun onInitChart() {
            itemView.lineChart.description.isEnabled = false
            itemView.lineChart.legend.isEnabled = false

            val xAxis = itemView.lineChart.xAxis
            xAxis.isEnabled = false

            val yAxis = itemView.lineChart.axisLeft
            yAxis.isEnabled = false

            val yAxis1 = itemView.lineChart.axisRight
            yAxis1.isEnabled = false
        }

        private fun bindChart(chartsList: List<SnapshotChart>) {
            rxCalculate(chartsList)
                    .delay(1500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ lineData ->
                        itemView.lineChart.clear()
                        itemView.lineChart.data = lineData
                        itemView.lineChart.invalidate()
                    }, {

                    })
        }

        private fun rxCalculate(chartsList: List<SnapshotChart>): Single<LineData> {
            return Single.fromCallable {
                val entries = ArrayList<Entry>()
                for (i in chartsList.indices) {
                    entries.add(BarEntry(i.toFloat(),
                            java.lang.Float.parseFloat(chartsList[i].price)))
                }

                val set = LineDataSet(entries, "")
                set.setDrawCircles(false)
                set.mode = LineDataSet.Mode.CUBIC_BEZIER
                set.lineWidth = 1f
                val lineData = LineData(set)
                lineData.setDrawValues(false)
                lineData.isHighlightEnabled = false
                lineData
            }
        }
    }
}
