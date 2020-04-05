package workshop.akbolatss.xchangesrates.screens.charts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_btn.view.*
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.utils.Constants.HOUR_1
import workshop.akbolatss.xchangesrates.utils.Constants.HOUR_12
import workshop.akbolatss.xchangesrates.utils.Constants.HOUR_24
import workshop.akbolatss.xchangesrates.utils.Constants.HOUR_3
import workshop.akbolatss.xchangesrates.utils.Constants.MINUTES_10
import workshop.akbolatss.xchangesrates.utils.Constants.MONTH
import workshop.akbolatss.xchangesrates.utils.Constants.MONTH_3
import workshop.akbolatss.xchangesrates.utils.Constants.MONTH_6
import workshop.akbolatss.xchangesrates.utils.Constants.WEEK
import workshop.akbolatss.xchangesrates.utils.Constants.YEAR_1
import workshop.akbolatss.xchangesrates.utils.Constants.YEAR_2
import workshop.akbolatss.xchangesrates.utils.Constants.YEAR_5
import java.util.*

/**
 * Author: Akbolat Sadvakassov
 * Date: 15.12.2017
 */

class HorizontalBtnsAdapter(private val mBtnIds: List<String>?, private var mSelectedPos: Int, private val mListener: OnBtnClickListener) : RecyclerView.Adapter<HorizontalBtnsAdapter.HorizontalBtnsVH>() {

    private val mItemViewList: MutableList<View>

    private val mInternalListener = View.OnClickListener { view ->
        val id = view.getTag(R.integer.key_id).toString()
        mSelectedPos = view.getTag(R.integer.key_pos) as Int
        mListener.onBtnClick(id, mSelectedPos)

        onUpdateBtns(mSelectedPos)
    }

    init {
        this.mItemViewList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalBtnsVH {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_btn, parent, false)
        return HorizontalBtnsVH(view)
    }

    override fun onBindViewHolder(holder: HorizontalBtnsVH, position: Int) {
        if (mSelectedPos == position) {
            holder.bind(mBtnIds!![position],
                    true, mListener)
        } else {
            holder.bind(mBtnIds!![position],
                    false, mListener)
        }

        holder.itemView.setOnClickListener(mInternalListener)
        holder.itemView.setTag(R.integer.key_id, mBtnIds[position])
        holder.itemView.setTag(R.integer.key_pos, position)
        mItemViewList.add(holder.itemView)
    }

    private fun onUpdateBtns(selectedPos: Int) {
        for (i in mItemViewList.indices) {
            mItemViewList[i].isSelected = selectedPos == i
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mBtnIds?.size ?: 0
    }

    interface OnBtnClickListener {
        fun onBtnClick(id: String, pos: Int)
    }

    inner class HorizontalBtnsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(s: String, isSelected: Boolean, mListener: OnBtnClickListener) {
            var str = ""
            val context = itemView.context
            when (s) {
                MINUTES_10 -> str = context.resources.getString(R.string.tv10min)
                HOUR_1 -> str = context.resources.getString(R.string.tv1h)
                HOUR_3 -> str = context.resources.getString(R.string.tv3h)
                HOUR_12 -> str = context.resources.getString(R.string.tv12h)
                HOUR_24 -> str = context.resources.getString(R.string.tv24h)
                WEEK -> str = context.resources.getString(R.string.tv1w)
                MONTH -> str = context.resources.getString(R.string.tv1m)
                MONTH_3 -> str = context.resources.getString(R.string.tv3m)
                MONTH_6 -> str = context.resources.getString(R.string.tv6m)
                YEAR_1 -> str = context.resources.getString(R.string.tv1y)
                YEAR_2 -> str = context.resources.getString(R.string.tv2y)
                YEAR_5 -> str = context.resources.getString(R.string.tv5y)
            }
            itemView.btnChartTiming.isSelected = isSelected
            itemView.btnChartTiming.text = str
        }
    }
}
