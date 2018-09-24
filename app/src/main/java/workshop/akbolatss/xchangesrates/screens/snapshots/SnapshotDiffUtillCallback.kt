package workshop.akbolatss.xchangesrates.screens.snapshots

import android.support.v7.util.DiffUtil
import workshop.akbolatss.xchangesrates.model.response.ChartData

class SnapshotDiffUtillCallback(private val oldList: List<ChartData> = ArrayList(),
                                private val newList: List<ChartData> = ArrayList()) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldSnapshot = oldList[oldItemPosition]
        val newSnapshot = newList[newItemPosition]
        return oldSnapshot.id == newSnapshot.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldSnapshot = oldList[oldItemPosition]
        val newSnapshot = newList[newItemPosition]
        return oldSnapshot.info.last == newSnapshot.info.last
    }

}