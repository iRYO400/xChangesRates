package workshop.akbolatss.xchangesrates.screens.notifications

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.format.DateFormat
import android.widget.TimePicker

import java.util.Calendar

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.01.2018
 */

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var isEditing: Boolean = false
    private var mItemPos: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isEditing = arguments!!.getBoolean("isEditing", false)
        mItemPos = arguments!!.getInt("itemPos", 0)

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute,
                DateFormat.is24HourFormat(activity))
    }

    interface TimePickerListener {
        fun onAddTime(hour: Int, minute: Int)

        fun onEditTime(hour: Int, minute: Int, itemPos: Int)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val listener = targetFragment as TimePickerListener?
        if (isEditing) {
            listener!!.onEditTime(hourOfDay, minute, mItemPos)
        } else {
            listener!!.onAddTime(hourOfDay, minute)
        }
    }

    companion object {

        fun newInstance(isEditing: Boolean, itemPos: Int): TimePickerFragment {
            val fragment = TimePickerFragment()
            val arg = Bundle()
            arg.putBoolean("isEditing", isEditing)
            arg.putInt("itemPos", itemPos)
            fragment.arguments = arg
            return fragment
        }
    }
}
