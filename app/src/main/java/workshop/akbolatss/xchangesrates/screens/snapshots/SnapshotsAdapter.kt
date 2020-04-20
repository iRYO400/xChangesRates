package workshop.akbolatss.xchangesrates.screens.snapshots

import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.BaseRVA
import workshop.akbolatss.xchangesrates.base.DataBoundViewHolder
import workshop.akbolatss.xchangesrates.databinding.ItemSnapshotBinding
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.utils.chart.setupChartInList

class SnapshotsAdapter(
    private val itemClickListener: (Long, Int) -> Unit,
    private val toggleNotificationListener: (Long) -> Unit,
    private val showOptionsClickListener: (Long) -> Unit,
    private val longClickListener: (Long, Int) -> Unit
) : BaseRVA<Snapshot>(DIFF_CALLBACK) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        val viewBinding = DataBindingUtil.inflate<ItemSnapshotBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_snapshot,
            parent, false
        )
        viewBinding.lineChart.setupChartInList()
        return viewBinding
    }

    override fun bind(holder: DataBoundViewHolder, item: Snapshot) {
        when (holder.binding) {
            is ItemSnapshotBinding -> with(holder.binding) {
                model = item

                snapshotView.setOnLongClickListener {
                    it.performHapticFeedback(
                        HapticFeedbackConstants.LONG_PRESS,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    )
                    longClickListener(item.id, holder.layoutPosition)
                    true
                }
                snapshotView.setOnClickListener {
                    itemClickListener(item.id, holder.layoutPosition)
                }
                btnToggleNotificationState.setOnClickListener {
                    toggleNotificationListener(item.id)
                }
                btnOptions.setOnClickListener {
                    showOptionsClickListener(item.id)
                }
            }
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
                    oldItem.low == newItem.low &&
                    oldItem.change == newItem.change &&
                    oldItem.updateTime == newItem.updateTime &&
                    oldItem.options.isNotificationEnabled == newItem.options.isNotificationEnabled
        }

    }
