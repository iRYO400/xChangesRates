package workshop.akbolatss.xchangesrates.screens.snapshots

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.fragment_options.*
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.utils.Constants

/**
 * Author: Akbolat Sadvakassov
 * Date: 18.01.2018
 */

class OptionsDialogFragment : DialogFragment() {


    private var mNotifiesCount: Int = 0

    private val timingCode: String
        get() {
            when (spinTime.selectedItemPosition) {
                0 -> return Constants.MINUTES_10
                1 -> return Constants.HOUR_1
                2 -> return Constants.HOUR_3
                3 -> return Constants.HOUR_12
                4 -> return Constants.HOUR_24
                else -> return Constants.HOUR_24
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layoutInflater = LayoutInflater.from(activity)
        val view = layoutInflater.inflate(R.layout.fragment_options, null)

        val alertDialogBuilder = AlertDialog.Builder(activity!!, R.style.CustomDialog)
        alertDialogBuilder.setView(view)

        val viewTitle = layoutInflater.inflate(R.layout.dialog_options_title, null)
        alertDialogBuilder.setCustomTitle(viewTitle)

        val arrayAdapter = ArrayAdapter(view.getContext(),
                R.layout.custom_spinner_item, view.getResources().getStringArray(R.array.array_time))
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        spinTime.adapter = arrayAdapter

        val isActive = arguments!!.getBoolean(Constants.BUNDLE_ISACTIVE, false)
        val timing = arguments!!.getString(Constants.BUNDLE_TIMING, Constants.HOUR_24)

        switchNotify.setChecked(isActive)
        spinTime.setSelection(getTimingPos(timing))

        alertDialogBuilder.setPositiveButton(R.string.alert_save, DialogInterface.OnClickListener { dialog, which ->
            val mListener = targetFragment as OptionsDialogListener?
            val chartId = arguments!!.getLong(Constants.BUNDLE_CHART_ID)
            val pos = arguments!!.getInt(Constants.BUNDLE_POSITION)
            val isChecked = switchNotify.isChecked
            val timingCode = timingCode
            mListener!!.onSaveChanges(chartId, isChecked, timingCode, pos)
        })

        alertDialogBuilder.setNegativeButton(R.string.alert_cancel, DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        alertDialogBuilder.setNeutralButton(R.string.alert_remove, DialogInterface.OnClickListener { dialog, which ->
            val mListener = targetFragment as OptionsDialogListener?
            val chartId = arguments!!.getLong(Constants.BUNDLE_CHART_ID)
            val pos = arguments!!.getInt(Constants.BUNDLE_POSITION)
            mListener!!.onRemove(chartId, pos)
        })

        mNotifiesCount = Hawk.get(Constants.HAWK_NOTIFIES_COUNT, 0)
        tvNotifiesCount.setText(mNotifiesCount.toString() + getString(R.string.tvNotifiesLimit))

        if (mNotifiesCount >= 5 && !switchNotify.isChecked()) {
            switchNotify.setEnabled(false)
        }

        switchNotify.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mNotifiesCount++
                tvNotifiesCount.text = mNotifiesCount.toString() + getString(R.string.tvNotifiesLimit)
            } else {
                mNotifiesCount--
                tvNotifiesCount.text = mNotifiesCount.toString() + getString(R.string.tvNotifiesLimit)
            }
            if (mNotifiesCount >= 5 && !isChecked) {
                switchNotify.isEnabled = false
            } else if (mNotifiesCount >= 5 && isChecked) {
                switchNotify.isEnabled = true
            } else if (mNotifiesCount < 5) {
                switchNotify.isEnabled = true
            }
        })
        return alertDialogBuilder.create()
    }

    interface OptionsDialogListener {
        fun onSaveChanges(chartId: Long, isActive: Boolean, timing: String, pos: Int)

        fun onRemove(chartId: Long, pos: Int)
    }

    private fun getTimingPos(timing: String): Int {
        when (timing) {
            Constants.MINUTES_10 -> return 0
            Constants.HOUR_1 -> return 1
            Constants.HOUR_3 -> return 2
            Constants.HOUR_12 -> return 3
            Constants.HOUR_24 -> return 4
            else -> return 4
        }
    }

    companion object {

        fun newInstance(chartId: Long, isActive: Boolean, timing: String, pos: Int): OptionsDialogFragment {
            val fragment = OptionsDialogFragment()
            val arg = Bundle()
            arg.putLong(Constants.BUNDLE_CHART_ID, chartId)
            arg.putBoolean(Constants.BUNDLE_ISACTIVE, isActive)
            arg.putString(Constants.BUNDLE_TIMING, timing)
            arg.putInt(Constants.BUNDLE_POSITION, pos)
            fragment.arguments = arg
            return fragment
        }
    }
}
