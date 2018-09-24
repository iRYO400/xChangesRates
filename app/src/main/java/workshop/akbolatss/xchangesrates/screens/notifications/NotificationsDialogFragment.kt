package workshop.akbolatss.xchangesrates.screens.notifications

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_notifications.*
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.model.dao.GlobalNotification
import workshop.akbolatss.xchangesrates.repositories.DBNotificationRepository

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.01.2018
 */

class NotificationsDialogFragment : DialogFragment(), NotificationsAdapter.NotificationListener, TimePickerFragment.TimePickerListener, DialogInterface.OnClickListener {

    private var mContext: Context? = null

    private var mAdapter: NotificationsAdapter? = null

    private var mRepository: DBNotificationRepository? = null


    companion object {

        fun newInstance(): NotificationsDialogFragment {
            return NotificationsDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mContext = context
        val layoutInflater = LayoutInflater.from(activity)
        val view = layoutInflater.inflate(R.layout.fragment_notifications, null)

        val alertDialogBuilder = AlertDialog.Builder(activity!!, R.style.CustomDialog)
        alertDialogBuilder.setView(view)

        val viewTitle = layoutInflater.inflate(R.layout.dialog_notifications_title, null)
        alertDialogBuilder.setCustomTitle(viewTitle)

        mAdapter = NotificationsAdapter(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = mAdapter


//        mRepository = DBNotificationRepository((activity!!.application as ApplicationMain).daoSession)
//        mRepository!!.allNotifications
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe({ notifications ->
//                    mAdapter!!.onAddItems(notifications)
//                }, {
//
//                })

        alertDialogBuilder.setPositiveButton(R.string.alert_save, this)
        alertDialogBuilder.setNegativeButton(R.string.alert_cancel, this)
        return alertDialogBuilder.create()
    }

    fun onAddNotify() {
        val fragment = TimePickerFragment.newInstance(false, 0)
        fragment.setTargetFragment(this@NotificationsDialogFragment, 500)
        fragment.show(fragmentManager!!, "timePicker")
    }

    override fun onChangeTime(pos: Int) {
        val fragment = TimePickerFragment.newInstance(true, pos)
        fragment.setTargetFragment(this@NotificationsDialogFragment, 500)
        fragment.show(fragmentManager!!, "timePicker")
    }

    override fun onStateChanged(pos: Int, isActive: Boolean) {
        mAdapter!!.onUpdateState(isActive, pos)
    }

    override fun onRemoveTime(pos: Int) {
        mAdapter!!.onRemoveTime(pos)
    }

    override fun onAddTime(hour: Int, minute: Int) {
        val notification = GlobalNotification()
        notification.hour = hour
        notification.minutes = minute
        notification.isActive = true
        notification.buildName()
        mAdapter!!.onAddItem(notification)
        recyclerView.smoothScrollToPosition(mAdapter!!.itemCount - 1)
    }

    override fun onEditTime(hour: Int, minute: Int, itemPos: Int) {
        mAdapter!!.onUpdateTime(hour, minute, itemPos)
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            AlertDialog.BUTTON_POSITIVE -> {
                progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
//                mRepository!!.saveSnapshotChanges(mAdapter!!.notifications)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeOn(Schedulers.io())
//                        .subscribe({ notifications ->
//                            onActivateNotifications(notifications)
//                            //                                dialog.dismiss();
//                        }, {
//                        })
            }
            AlertDialog.BUTTON_NEGATIVE -> dialog.cancel()
        }
    }

    private fun onActivateNotifications(notifications: List<GlobalNotification>) {
        for (i in notifications.indices) {
        }
    }
}
