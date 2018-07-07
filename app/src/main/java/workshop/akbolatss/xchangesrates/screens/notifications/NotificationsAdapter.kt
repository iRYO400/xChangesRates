package workshop.akbolatss.xchangesrates.screens.notifications

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.rv_notification.view.*
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.model.dao.GlobalNotification
import java.util.*

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.01.2018
 */

class NotificationsAdapter(private val mListener: NotificationListener) : RecyclerView.Adapter<NotificationsAdapter.NotificationsVH>() {

    private val mNotifications: MutableList<GlobalNotification> = ArrayList()

    val notifications: List<GlobalNotification>
        get() = mNotifications

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsVH {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_notification, parent, false)
        return NotificationsVH(view)
    }

    fun onAddItem(notification: GlobalNotification) {
        mNotifications!!.add(notification)
        //        notifyItemInserted(mNotifications.size());
        notifyDataSetChanged()
    }

    fun onAddItems(list: List<GlobalNotification>?) {
        if (list != null) {
            mNotifications!!.clear()
            mNotifications.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun onUpdateTime(hour: Int, minute: Int, itemPos: Int) {
        val notification = mNotifications!![itemPos]
        notification.hour = hour
        notification.minutes = minute
        notification.buildName()
        mNotifications[itemPos] = notification
        notifyItemChanged(itemPos)
        //        notifyDataSetChanged();
        //        Log.d("TAG", "onUpdateTime");
    }

    fun onUpdateState(isActive: Boolean, itemPos: Int) {
        val notification = mNotifications!![itemPos]
        notification.isActive = isActive
        mNotifications[itemPos] = notification
        //        notifyItemChanged(itemPos);
        //        notifyDataSetChanged();
    }

    fun onRemoveTime(itemPos: Int) {
        mNotifications!!.removeAt(itemPos)
        //        notifyItemRemoved(itemPos);
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: NotificationsVH, position: Int) {
        holder.bind(mNotifications!![position], mListener)
    }

    override fun getItemCount(): Int {
        return mNotifications?.size ?: 0
    }

    interface NotificationListener {
        fun onChangeTime(pos: Int)

        fun onStateChanged(pos: Int, isActive: Boolean)

        fun onRemoveTime(pos: Int)
    }

    inner class NotificationsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(notification: GlobalNotification, listener: NotificationListener) {

            itemView.tvTiming.text = notification.name
            itemView.cbNotification.isChecked = notification.isActive

            itemView.cbNotification.setOnCheckedChangeListener({ buttonView, isChecked ->
                onUpdateState(isChecked, adapterPosition)
            })

            itemView.flTiming.setOnClickListener({ listener.onChangeTime(adapterPosition) })
        }
    }
}
