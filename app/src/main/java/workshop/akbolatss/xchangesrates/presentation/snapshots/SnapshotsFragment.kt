package workshop.akbolatss.xchangesrates.presentation.snapshots

import android.os.Bundle
import androidx.lifecycle.Observer
import com.orhanobut.hawk.Hawk
import kz.jgroup.pos.util.EventObserver
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.BaseFragment
import workshop.akbolatss.xchangesrates.base.DataBoundViewHolder
import workshop.akbolatss.xchangesrates.databinding.FragmentSnapshotsBinding
import workshop.akbolatss.xchangesrates.databinding.ItemSnapshotBinding
import workshop.akbolatss.xchangesrates.presentation.base.Error
import workshop.akbolatss.xchangesrates.presentation.base.Loading
import workshop.akbolatss.xchangesrates.presentation.base.Success
import workshop.akbolatss.xchangesrates.presentation.base.ViewState
import workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.details.SnapshotDetailsBottomDialog
import workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.options.OnSnapshotOptionsCallback
import workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.options.SnapshotOptionsDialog
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.extension.*


class SnapshotsFragment(
    override val layoutId: Int = R.layout.fragment_snapshots
) : BaseFragment<FragmentSnapshotsBinding>(),
    OnSnapshotOptionsCallback {

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
        adapter = SnapshotsAdapter(itemClickListener = { itemId: Long, _: Int ->
            openSnapshotDetails(itemId)
        }, showOptionsClickListener = { itemId ->
            openSnapshotOptions(itemId)
        }, toggleNotificationListener = { itemId: Long ->
            toggleNotification(itemId)
        }, longClickListener = { itemId: Long, position: Int ->
            viewModel.updateSingle(itemId, position)
        })
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    private fun openSnapshotDetails(snapshotId: Long) {
        val dialog = SnapshotDetailsBottomDialog.newInstance(snapshotId)
        dialog.show(requireFragmentManager(), dialog.tag)
    }

    private fun openSnapshotOptions(itemId: Long) {
        val dialog = SnapshotOptionsDialog.newInstance(itemId)
        dialog.show(childFragmentManager, dialog.tag)
    }

    override fun setObserversListeners() {
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.snapshots.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.updatingItemViewState.observe(viewLifecycleOwner,
            Observer { (loadingState, position) ->
                handleSnapshotLoadingState(loadingState, position)
            })
        viewModel.snapshot2ToggleNotification.observe(
            viewLifecycleOwner, EventObserver { snapshot ->
                if (snapshot.options.isNotificationEnabled) {
                    context.createNotificationChannel(snapshot)
                    context.launchWorker(snapshot)
                } else {
                    context.cancelWorker(snapshot)
                    context.deleteNotificationChannel(snapshot)
                }
            })
    }

    private fun handleSnapshotLoadingState(
        loadingState: ViewState,
        position: Int
    ) {
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
    }

    private fun toggleNotification(itemId: Long) {
        viewModel.toggleNotification(itemId)
    }

    fun updateAllSnapshots() {
        viewModel.updateAll()
    }

    override fun onSnapshotNotLoaded() {
        binding.coordinator.showSnackBar(getString(R.string.snapshot_notification_option_not_loaded_error))
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
