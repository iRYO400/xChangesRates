package workshop.akbolatss.xchangesrates.screens.snapshots

import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import com.orhanobut.hawk.Hawk
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.BaseFragment
import workshop.akbolatss.xchangesrates.base.DataBoundViewHolder
import workshop.akbolatss.xchangesrates.databinding.FragmentSnapshotsBinding
import workshop.akbolatss.xchangesrates.databinding.ItemSnapshotBinding
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.presentation.base.Error
import workshop.akbolatss.xchangesrates.presentation.base.Loading
import workshop.akbolatss.xchangesrates.presentation.base.Success
import workshop.akbolatss.xchangesrates.screens.snapshots.dialog.options.SnapshotOptionsDialog
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.extension.gone
import workshop.akbolatss.xchangesrates.utils.extension.invisible
import workshop.akbolatss.xchangesrates.utils.extension.visible


class SnapshotsFragment(
    override val layoutId: Int = R.layout.fragment_snapshots
) : BaseFragment<FragmentSnapshotsBinding>(),
    SnapshotOptionsDialog.OnSnapshotOptionsCallback {

    companion object {

        fun newInstance(): SnapshotsFragment {
            return SnapshotsFragment()
        }
    }

    private val viewModel by currentScope.viewModel<SnapshotsViewModel>(this)

    private lateinit var adapter: SnapshotsAdapter

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = SnapshotsAdapter(itemClickListener = { itemId: Long, position: Int ->
        }, longClickListener = { itemId: Long, position: Int ->
            viewModel.updateSingle(itemId, position)
        })
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    override fun setObserversListeners() {
        Handler().postDelayed({
            showStartupShowCase()
        }, 500)

        observeViewModel()
        setListeners()
    }

    private fun observeViewModel() {
        viewModel.snapshots.observe(viewLifecycleOwner, Observer {
            Timber.d("snapshots observe")
            adapter.submitList(it)
        })
        viewModel.updatingItemViewState.observe(viewLifecycleOwner,
            Observer { (loadingState, position) ->
                when (loadingState) {
                    is Loading -> {
                        binding.recyclerView.findViewHolderForLayoutPosition(position)?.apply {
                            if (this is DataBoundViewHolder && this.binding is ItemSnapshotBinding) {
                                binding.progressBar.visible()
                                binding.imgError.gone()
                                binding.tvTime.invisible()
                                binding.snapshotView.isEnabled = false
                                binding.snapshotView.isClickable = false
                            }
                        }
                    }
                    is Success<*> -> {
                        binding.recyclerView.findViewHolderForLayoutPosition(position)?.apply {
                            if (this is DataBoundViewHolder && this.binding is ItemSnapshotBinding) {
                                binding.progressBar.gone()
                                binding.imgError.gone()
                                binding.tvTime.visible()
                                binding.snapshotView.isEnabled = true
                                binding.snapshotView.isClickable = true
                            }
                        }
                    }
                    is Error -> {
                        binding.recyclerView.findViewHolderForLayoutPosition(position)?.apply {
                            if (this is DataBoundViewHolder && this.binding is ItemSnapshotBinding) {
                                binding.progressBar.gone()
                                binding.imgError.visible()
                                binding.tvTime.gone()
                                binding.snapshotView.isEnabled = true
                                binding.snapshotView.isClickable = true
                            }
                        }
                    }
                }
            })
    }

    private fun setListeners() {

    }

    private fun toggleNotification(chartData: ChartData, pos: Int) {
        val lastState = chartData.isNotificationEnabled
        chartData.isNotificationEnabled = !chartData.isNotificationEnabled
        saveSnapshotChanges(chartData, lastState, pos)
    }

    private fun openSnapshotOptionsDialog(chartId: Long, pos: Int) {
        val dialog = SnapshotOptionsDialog.newInstance(chartId, pos)
        dialog.show(childFragmentManager, dialog.tag)
    }

    /**
     * Save edited changes
     */
    override fun saveSnapshotChanges(
        chartData: ChartData,
        lastNotificationState: Boolean,
        pos: Int
    ) {

    }

    fun updateAllSnapshots() {
        if (adapter.itemCount <= 0) {
            return
        }

        viewModel.updateAll()
    }

    /**
     * Show ShowCase at startup
     */
    private fun showStartupShowCase() {
        if (!Hawk.get(Constants.HAWK_SHOWCASE_0_DONE, false)) {
            val showCaseQueue: FancyShowCaseQueue
            val showCase1 = FancyShowCaseView.Builder(activity!!)
                .title(resources.getString(R.string.showcase_snap_1))
//                .backgroundColor(R.color.colorShowCaseBG)
                .build()

            showCaseQueue = FancyShowCaseQueue()
                .add(showCase1)

            showCaseQueue.show()
            Hawk.put(Constants.HAWK_SHOWCASE_0_DONE, true)
        }
    }

}
