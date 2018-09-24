package workshop.akbolatss.xchangesrates.screens.snapshots

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import net.cachapa.expandablelayout.ExpandableLayout
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.app.ApplicationMain
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.room.ChartDataDao
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.Logger
import java.util.concurrent.TimeUnit

class OptionsDialogFragment : DialogFragment() {

    private lateinit var mCompositeDisposable: CompositeDisposable

    private lateinit var mChartDataDao: ChartDataDao

    private lateinit var mChartData: ChartData

    private var mLastNotificationState: Boolean = false

    companion object {

        fun newInstance(chartId: Long, pos: Int): OptionsDialogFragment {
            val fragment = OptionsDialogFragment()
            val arg = Bundle()
            arg.putLong(Constants.BUNDLE_CHART_ID, chartId)
            arg.putInt(Constants.BUNDLE_POSITION, pos)
            fragment.arguments = arg
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layoutInflater = LayoutInflater.from(activity)
        val view = layoutInflater.inflate(R.layout.fragment_options, null)

        val alertDialogBuilder = AlertDialog.Builder(activity!!, R.style.CustomDialog)
        alertDialogBuilder.setView(view)

        alertDialogBuilder.setPositiveButton(R.string.alert_save) { dialog, which ->
            val mListener = targetFragment as OptionsDialogListener?
            val pos = arguments!!.getInt(Constants.BUNDLE_POSITION)
            Logger.i("Interval Index: ${mChartData.options.intervalUpdateIndex}")
            mListener!!.saveSnapshotChanges(mChartData, mLastNotificationState, pos)
        }
        alertDialogBuilder.setNeutralButton(R.string.alert_remove) { dialog, which ->
            val mListener = targetFragment as OptionsDialogListener?
            val pos = arguments!!.getInt(Constants.BUNDLE_POSITION)
            mListener!!.removeSnapshot(mChartData, pos)
        }

        mCompositeDisposable = CompositeDisposable()
        mChartDataDao = ApplicationMain.instance.appDatabase.chartDataDao()
        mCompositeDisposable.add(mChartDataDao.getById(arguments!!.getLong(Constants.BUNDLE_CHART_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mChartData = it
                    mLastNotificationState = mChartData.isNotificationEnabled
                    fillView(mChartData, view)
//                    if (dialog != null) {
//                        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
//                        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEUTRAL).isEnabled = true
//                    }
                }, {

                })
        )

        val alertDialog = alertDialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return alertDialog
    }

    override fun onResume() {
        super.onResume()
//        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
//        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEUTRAL).isEnabled = false
    }

    private fun fillView(chartData: ChartData, view: View) {
        var fixer = false
        Logger.i("fillView Interval Index: ${chartData.options.intervalUpdateIndex}")
        val seekBarHistoryTiming = view.findViewById<SeekBar>(R.id.seekHistoryTiming)
        val tvHistoryTiming = view.findViewById<TextView>(R.id.tvHistoryTiming)
        val historyTimingArray = resources.getStringArray(R.array.array_history_timing)
        val switchNotification = view.findViewById<Switch>(R.id.switchNotify)
        val expandableLayout = view.findViewById<ExpandableLayout>(R.id.expandableLayout)

        val seekBarInterval = view.findViewById<SeekBar>(R.id.seekIntervalTiming)
        val tvIntervalUpdates = view.findViewById<TextView>(R.id.tvIntervalUpdates)
        val intervalUpdatesArray = resources.getStringArray(R.array.array_interval_updates)
        val intervalSmartUpdatesArray = resources.getStringArray(R.array.array_interval_updates_smart)

        val switchSmart = view.findViewById<Switch>(R.id.switchSmart)

        seekBarHistoryTiming.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvHistoryTiming.text = historyTimingArray[progress]
                mChartData.timingIndex = progress
                mChartData.timingName = getHistoryTimingName(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        seekBarHistoryTiming.progress = chartData.timingIndex
        tvHistoryTiming.text = historyTimingArray[chartData.timingIndex]

        switchNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                expandableLayout.expand()
            } else {
                expandableLayout.collapse()
            }
            mChartData.isNotificationEnabled = isChecked
        }
        switchNotification.isChecked = chartData.isNotificationEnabled

        switchSmart.setOnCheckedChangeListener { buttonView, isChecked ->
            mChartData.options.isSmartEnabled = isChecked

            if (isChecked) {
                seekBarInterval.progress = seekBarInterval.progress - 8
                seekBarInterval.max = 9
            } else {
                seekBarInterval.max = 17
                seekBarInterval.progress = seekBarInterval.progress + 8
            }
        }

        switchSmart.isChecked = chartData.options.isSmartEnabled

        seekBarInterval.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fixer) {
                    return
                }
                if (switchSmart.isChecked) {
                    tvIntervalUpdates.text = intervalSmartUpdatesArray[progress]
                    mChartData.options.intervalUpdateIndex = progress + 8
                } else {
                    tvIntervalUpdates.text = intervalUpdatesArray[progress]
                    mChartData.options.intervalUpdateIndex = progress
                }

                Logger.i("SeekBar Interval Index: ${mChartData.options.intervalUpdateIndex}")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        if (chartData.options.isSmartEnabled) {
            seekBarInterval.max = 9
            tvIntervalUpdates.text = intervalSmartUpdatesArray[chartData.options.intervalUpdateIndex - 8]
            seekBarInterval.progress = chartData.options.intervalUpdateIndex - 8
        } else {
            seekBarInterval.max = 17
            tvIntervalUpdates.text = intervalUpdatesArray[chartData.options.intervalUpdateIndex]
            seekBarInterval.progress = chartData.options.intervalUpdateIndex
        }

        Logger.i("End of Interval Index: ${mChartData.options.intervalUpdateIndex}")
        fixer = true
    }

    private fun getHistoryTimingName(pos: Int): String {
        when (pos) {
            0 -> {
                return "10min"
            }
            1 -> {
                return "1h"
            }
            2 -> {
                return "3h"
            }
            3 -> {
                return "12h"
            }
            4 -> {
                return "24h"
            }
            5 -> {
                return "1w"
            }
            6 -> {
                return "1m"
            }
            7 -> {
                return "3m"
            }
            8 -> {
                return "6m"
            }
            9 -> {
                return "1y"
            }
            10 -> {
                return "2y"
            }
            11 -> {
                return "5y"
            }
        }
        return "10min"
    }

    override fun onDismiss(dialog: DialogInterface?) {
        if (::mCompositeDisposable.isInitialized)
            mCompositeDisposable.clear()
        super.onDismiss(dialog)
    }

    interface OptionsDialogListener {
        fun saveSnapshotChanges(chartData: ChartData, lastNotificationState: Boolean, pos: Int)

        fun removeSnapshot(chartData: ChartData, pos: Int)
    }
}
