package workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.options

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Observer
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.dialog.BaseDialogFragment
import workshop.akbolatss.xchangesrates.databinding.FragmentOptionsBinding
import workshop.akbolatss.xchangesrates.utils.extension.defaultVal
import workshop.akbolatss.xchangesrates.utils.widget.PopupView

class SnapshotOptionsDialog : BaseDialogFragment<FragmentOptionsBinding>(
    layoutId = R.layout.fragment_options
) {

    companion object {

        const val PARAM_SNAPSHOT_ID = "_snapshotId"

        fun newInstance(snapshotId: Long): SnapshotOptionsDialog {
            val fragment = SnapshotOptionsDialog()
            val arg = Bundle()
            arg.putLong(PARAM_SNAPSHOT_ID, snapshotId)
            fragment.arguments = arg
            return fragment
        }
    }

    private val viewModel by currentScope.viewModel<SnapshotOptionsViewModel>(this) {
        parametersOf(arguments?.getLong(PARAM_SNAPSHOT_ID) ?: defaultVal())
    }

    private val chartChangesPeriodHintArray by lazy {
        resources.getStringArray(R.array.array_history_timing)
    }

    private val updateIntervalHintArray by lazy {
        resources.getStringArray(R.array.array_interval_updates_smart)
    }

    private lateinit var callback: OnSnapshotOptionsCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is OnSnapshotOptionsCallback)
            callback = parentFragment as OnSnapshotOptionsCallback
        else
            throw RuntimeException("Caller must implement ${OnSnapshotOptionsCallback::class.java.simpleName}")
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        binding.viewModel = viewModel
    }

    override fun setObserversListeners() {
        observeViewModel()
        setListeners()
    }

    private fun observeViewModel() {
        viewModel.chartChangesPeriod.observe(viewLifecycleOwner, Observer {
            it?.let { index ->
                viewModel.selectedChangesPeriod.value = chartChangesPeriodHintArray[index]
            }
        })
        viewModel.updateInterval.observe(viewLifecycleOwner, Observer {
            it?.let { index ->
                viewModel.selectedUpdateInterval.value = updateIntervalHintArray[index]
            }
        })
        viewModel.snapshotError.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let {
                callback.onSnapshotNotLoaded()
            }
        })
        viewModel.optionsUpdated.observe(viewLifecycleOwner, Observer {
            if (it)
                dismiss()
        })
    }

    private fun setListeners() {
        binding.btnPeriodInfo.setOnClickListener { view ->
            PopupView.show(view, getString(R.string.snapshot_notification_option_chart_period))
        }
        binding.btnNotificationInfo.setOnClickListener { view ->
            PopupView.show(view, getString(R.string.snapshot_notification_option_update_interval))
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnSave.setOnClickListener {
            viewModel.save()
        }
    }

}

interface OnSnapshotOptionsCallback {

    fun onSnapshotNotLoaded()

}
