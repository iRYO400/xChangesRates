package workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.databinding.DialogDetailsBinding
import workshop.akbolatss.xchangesrates.utils.chart.setupChart
import workshop.akbolatss.xchangesrates.utils.extension.defaultVal

class SnapshotDetailsBottomDialog : BottomSheetDialogFragment() {

    companion object {
        const val PARAM_SNAPSHOT_ID = "_snapshotId"

        fun newInstance(snapshotId: Long): SnapshotDetailsBottomDialog {
            val fragment = SnapshotDetailsBottomDialog()
            val arg = Bundle()
            arg.putLong(PARAM_SNAPSHOT_ID, snapshotId)
            fragment.arguments = arg
            return fragment
        }
    }

    private val viewModel by currentScope.viewModel<SnapshotDetailsViewModel>(this) {
        parametersOf(arguments?.getLong(PARAM_SNAPSHOT_ID) ?: defaultVal())
    }

    private lateinit var binding: DialogDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_details, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        init()
        return binding.root
    }

    private fun init() {
        binding.viewModel = viewModel
        binding.lineChart.setupChart(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}
