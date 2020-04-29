package workshop.akbolatss.xchangesrates.presentation.snapshots

import android.os.Bundle
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kz.jgroup.pos.util.Event
import kz.jgroup.pos.util.EventObserver
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.BaseFragment
import workshop.akbolatss.xchangesrates.base.DataBoundViewHolder
import workshop.akbolatss.xchangesrates.databinding.FragmentSnapshotsBinding
import workshop.akbolatss.xchangesrates.databinding.ItemSnapshotBinding
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.presentation.base.Error
import workshop.akbolatss.xchangesrates.presentation.base.Loading
import workshop.akbolatss.xchangesrates.presentation.base.Success
import workshop.akbolatss.xchangesrates.presentation.base.ViewState
import workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.details.SnapshotDetailsBottomDialog
import workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.options.OnSnapshotOptionsCallback
import workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.options.SnapshotOptionsDialog
import workshop.akbolatss.xchangesrates.utils.extension.*


class SnapshotListFragment(
    override val layoutId: Int = R.layout.fragment_snapshots
) : BaseFragment<FragmentSnapshotsBinding>(),
    OnSnapshotOptionsCallback {

    companion object {

        fun newInstance(): SnapshotListFragment {
            return SnapshotListFragment()
        }
    }

    private val viewModel by currentScope.viewModel<SnapshotsViewModel>(this)

    private lateinit var interstitialOnSnapshotDetails: InterstitialAd

    private lateinit var adapter: SnapshotsAdapter

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        initRecyclerView()
        preloadInterstitialAd()
    }

    private fun initRecyclerView() {
        adapter = SnapshotsAdapter(itemClickListener = { itemId: Long, _: Int ->
            viewModel.updateSnapshotClickTimes()
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

    override fun onSnapshotNotLoaded() {
        binding.coordinator.showSnackBar(getString(R.string.snapshot_notification_option_not_loaded_error))
    }

    override fun onSnapshotUpdated(snapshot: Snapshot) {
        viewModel.snapshot2ToggleNotification.value = Event(snapshot)
    }

    private fun preloadInterstitialAd() {
        interstitialOnSnapshotDetails = InterstitialAd(_mActivity).apply {
            adUnitId = getString(R.string.snapshotsListInterstitial)
            loadAd(AdRequest.Builder().build())
            adListener = object : AdListener() {
                override fun onAdClosed() {
                    loadAd(AdRequest.Builder().build())
                }
            }
        }
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
        viewModel.snapshotClickedTimes.observe(viewLifecycleOwner, EventObserver {
            if (it.rem(3) == 0)
                showInterstitialAd()
        })
    }

    private fun handleSnapshotLoadingState(
        viewState: ViewState,
        position: Int
    ) {
        when (viewState) {
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

    private fun showInterstitialAd() {
        if (::interstitialOnSnapshotDetails.isInitialized &&
            interstitialOnSnapshotDetails.isLoaded
        )
            interstitialOnSnapshotDetails.show()
        else
            Timber.e("InterstitialAd in SnapshotDetails not loaded")
    }

    fun updateAllSnapshots() {
        viewModel.updateAll()
    }

}
