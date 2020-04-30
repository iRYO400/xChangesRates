package workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.details

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.databinding.DialogDetailsBinding
import workshop.akbolatss.xchangesrates.presentation.base.Error
import workshop.akbolatss.xchangesrates.presentation.base.Loading
import workshop.akbolatss.xchangesrates.presentation.base.Success
import workshop.akbolatss.xchangesrates.presentation.chart.PeriodSelectorAdapter
import workshop.akbolatss.xchangesrates.presentation.model.ChartPeriod
import workshop.akbolatss.xchangesrates.utils.chart.setupLineChartInSnapshotDetails
import workshop.akbolatss.xchangesrates.utils.extension.defaultVal
import workshop.akbolatss.xchangesrates.utils.extension.gone
import workshop.akbolatss.xchangesrates.utils.extension.showSnackBar
import workshop.akbolatss.xchangesrates.utils.extension.visible
import workshop.akbolatss.xchangesrates.utils.fragment.argumentNullable
import java.util.*

class SnapshotDetailsBottomDialog : BottomSheetDialogFragment() {

    private var snapshotId: Long? by argumentNullable()

    companion object {
        fun newInstance(snapshotId: Long): SnapshotDetailsBottomDialog =
            SnapshotDetailsBottomDialog().apply {
                this.snapshotId = snapshotId
            }
    }

    private val viewModel by currentScope.viewModel<SnapshotDetailsViewModel>(this) {
        parametersOf(snapshotId ?: defaultVal())
    }

    private lateinit var binding: DialogDetailsBinding

    private lateinit var adapter: PeriodSelectorAdapter

    private var isFragmentDraggable = false

    private lateinit var callback: OnSnapshotDetailsCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is OnSnapshotDetailsCallback)
            callback = parentFragment as OnSnapshotDetailsCallback
        else
            throw IllegalStateException("Caller must implement ${OnSnapshotDetailsCallback::class.java.simpleName}")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener(::onShow)
        }
    }

    private fun onShow(dialogInterface: DialogInterface) {
        val dialog = dialogInterface as BottomSheetDialog
        val frameLayout =
            dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                ?: return

        BottomSheetBehavior.from(frameLayout).apply {
            this.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING && !isFragmentDraggable)
                        state = BottomSheetBehavior.STATE_EXPANDED
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (slideOffset >= -0.2 && state != BottomSheetBehavior.STATE_DRAGGING)
                        state = BottomSheetBehavior.STATE_EXPANDED
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_details, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewLifecycleOwner = viewLifecycleOwner
        init()
        return binding.root
    }

    private fun init() {
        binding.viewModel = viewModel
        binding.lineChart.setupLineChartInSnapshotDetails(requireContext()) {
            viewModel.charts.value?.let { list ->
                val priceByTime = list[it.x.toInt()]
                viewModel.selectedChartDotRate.value = priceByTime.price
                viewModel.selectedChartDotTime.value = Date(priceByTime.timestamp * 1000)
            }
        }
        initRV()
    }

    private fun initRV() {
        adapter = PeriodSelectorAdapter { historyButton, _ ->
            highlightSelected(historyButton)
        }
        binding.recyclerView.adapter = adapter
    }

    private fun highlightSelected(historyButton: ChartPeriod) {
        viewModel.toggleSelected(historyButton)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setListeners()
    }

    private fun observeViewModel() {
        viewModel.snapshotLoadState.observe(viewLifecycleOwner, Observer {
            if (it is Error)
                dismiss()
        })
        viewModel.snapshotUpdateState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Loading -> binding.progressBar.visible()
                is Success<*> -> binding.progressBar.gone()
                is Error -> {
                    binding.coordinator.showSnackBar(R.string.snapshot_details_load_by_period)
                    binding.progressBar.gone()
                }
            }
        })
        viewModel.chartPeriodList.observe(viewLifecycleOwner, Observer { chartPeriodList ->
            adapter.submitList(chartPeriodList)
        })
        viewModel.selectedPeriod.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.tryUpdateSnapshot()
            }
        })

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        binding.vDragArea.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isFragmentDraggable = true
                }
                MotionEvent.ACTION_UP or MotionEvent.ACTION_MOVE or MotionEvent.ACTION_CANCEL -> {
                    isFragmentDraggable = false
                }
            }
            true
        }

        binding.ivDelete.setOnClickListener {
            callback.deleteSnapshot(viewModel.snapshotId)
            dismiss()
        }
    }

    interface OnSnapshotDetailsCallback {

        fun deleteSnapshot(snapshotId: Long)
    }
}
