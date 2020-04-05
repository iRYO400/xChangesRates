package workshop.akbolatss.xchangesrates.screens.snapshots.dialog.options

import android.content.Context
import android.os.Bundle
import android.widget.SeekBar
import androidx.lifecycle.Observer
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.dialog.BaseDialogFragment
import workshop.akbolatss.xchangesrates.databinding.FragmentOptionsBinding
import workshop.akbolatss.xchangesrates.model.response.ChartData

class SnapshotOptionsDialog : BaseDialogFragment<FragmentOptionsBinding>(
        layoutId = R.layout.fragment_options
) {

    companion object {

        const val PARAM_ITEM_ID = "_itemId"

        fun newInstance(chartId: Long, pos: Int): SnapshotOptionsDialog {
            val fragment = SnapshotOptionsDialog()
            val arg = Bundle()
            arg.putLong(PARAM_ITEM_ID, chartId)
            fragment.arguments = arg
            return fragment
        }
    }

    private val viewModel by currentScope.viewModel<SnapshotOptionsViewModel>(this) {
        parametersOf(arguments?.getLong(PARAM_ITEM_ID))
    }

    private lateinit var callback: OnSnapshotOptionsCallback

    private val historyTimingArray by lazy {
        resources.getStringArray(R.array.array_history_timing)
    }
    private val intervalUpdatesArray by lazy {
        resources.getStringArray(R.array.array_interval_updates)
    }
    private val intervalSmartUpdatesArray by lazy {
        resources.getStringArray(R.array.array_interval_updates_smart)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is OnSnapshotOptionsCallback)
            callback = parentFragment as OnSnapshotOptionsCallback
        else
            throw RuntimeException("Must implement OnSnapshotOptionsCallback")
    }

    override fun setObserversListeners() {
        observeViewModel()
        setListeners()
    }

    private fun observeViewModel() {
        viewModel.snapshot.observe(viewLifecycleOwner, Observer {
            it?.let { chartData ->
                if (chartData.options.isSmartEnabled) {
                    binding.seekIntervalTiming.max = 9
                    binding.tvIntervalUpdates.text = intervalSmartUpdatesArray[chartData.options.intervalUpdateIndex - 8]
                    binding.seekIntervalTiming.progress = chartData.options.intervalUpdateIndex - 8
                } else {
                    binding.seekIntervalTiming.max = 17
                    binding.tvIntervalUpdates.text = intervalUpdatesArray[chartData.options.intervalUpdateIndex]
                    binding.seekIntervalTiming.progress = chartData.options.intervalUpdateIndex
                }

                binding.seekHistoryTiming.progress = chartData.timingIndex
                val historyTimingArray = resources.getStringArray(R.array.array_history_timing)

                binding.tvHistoryTiming.text = historyTimingArray[chartData.timingIndex]
                binding.switchNotify.isChecked = chartData.isNotificationEnabled
                binding.switchSmart.isChecked = chartData.options.isSmartEnabled
            }
        })
    }

    private fun setListeners() {

        //        alertDialogBuilder.setPositiveButton(R.string.alert_save) { dialog, which ->
//            val mListener = targetFragment as OptionsDialogListener?
//            val pos = arguments!!.getInt(Constants.BUNDLE_POSITION)
//            mListener!!.saveSnapshotChanges(mChartData, mLastNotificationState, pos)
//        }
        binding.btnSaveChanges.setOnClickListener {
//            callback.saveSnapshotChanges()
        }
        binding.switchNotify.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.expandableLayout.expand()
            } else {
                binding.expandableLayout.collapse()
            }
//            mChartData.isNotificationEnabled = isChecked TODO
        }

        binding.switchSmart.setOnCheckedChangeListener { buttonView, isChecked ->
//            mChartData.options.isSmartEnabled = isChecked TODO

            if (isChecked) {
                binding.seekIntervalTiming.progress = binding.seekIntervalTiming.progress - 8
                binding.seekIntervalTiming.max = 9
            } else {
                binding.seekIntervalTiming.max = 17
                binding.seekIntervalTiming.progress = binding.seekIntervalTiming.progress + 8
            }
        }

        binding.seekHistoryTiming.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvHistoryTiming.text = historyTimingArray[progress]
//                mChartData.timingIndex = progress TODO
//                mChartData.timingName = getHistoryTimingName(progress) TODO
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        binding.seekIntervalTiming.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (binding.switchSmart.isChecked) {
                    binding.tvIntervalUpdates.text = intervalSmartUpdatesArray[progress]
//                    mChartData.options.intervalUpdateIndex = progress + 8 TODO
                } else {
                    binding.tvIntervalUpdates.text = intervalUpdatesArray[progress]
//                    mChartData.options.intervalUpdateIndex = progress TODO
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }

    interface OnSnapshotOptionsCallback {
        fun saveSnapshotChanges(chartData: ChartData, lastNotificationState: Boolean, pos: Int)
    }
}
