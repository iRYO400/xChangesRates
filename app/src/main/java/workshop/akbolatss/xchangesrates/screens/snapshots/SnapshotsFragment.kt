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
import workshop.akbolatss.xchangesrates.databinding.FragmentSnapshotsBinding
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.screens.snapshots.dialog.options.SnapshotOptionsDialog
import workshop.akbolatss.xchangesrates.utils.Constants


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
            viewModel.updateSingle(itemId, position)
        }, notificationToggleClickListener = { snapshot: Snapshot, position: Int ->
//            toggleNotification(chartData, position)
        }, openOptionsClickListener = { itemId: Long, positions: Int ->
            openSnapshotOptionsDialog(itemId, positions)
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
            Timber.d("observe snapshots $it")
            adapter.submitList(it)
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
//        if (lastNotificationState && chartData.isNotificationEnabled) { // Was enabled, then still enabled. Maybe changed timing
//            dequeueWorker(chartData)
//            UtilityMethods.clearOngoinNotification(chartData, _mActivity)
//            enqueueWorker(chartData)
//        }
//        if (lastNotificationState && !chartData.isNotificationEnabled) { // Was enabled, then disabled, So dequeue it
//            dequeueWorker(chartData)
//            UtilityMethods.clearOngoinNotification(chartData, _mActivity)
//        }
//        if (!lastNotificationState && chartData.isNotificationEnabled) { // Was disabled, then enabled. So only enqueue it
//            enqueueWorker(chartData)
//        }
//        if (!lastNotificationState && !chartData.isNotificationEnabled) { // Was disabled, then still disabled. Nothing to do
//        }

//        mPresenter.onUpdateOptions(chartData, pos)
    }

    /**
     * Remove snapshot from database
     */
//    override fun removeSnapshot(chartData: ChartData, pos: Int) {
//        dequeueWorker(chartData)
//        UtilityMethods.clearOngoinNotification(chartData, _mActivity)
//        UtilityMethods.deleteNotificationChannel(chartData, _mActivity)
//        mAdapter.notifyItemRemoved(pos)
//        mPresenter.onRemoveSnapshot(chartData.id)
//    }

//    override fun enqueueWorker(chartData: ChartData) {
//        val myConstraints = Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .setRequiresCharging(false)
//                .build()
//
//        val inputData = Data.Builder()
//                .putLong(WORKER_INPUT_ID, chartData.id)
//                .build()
//
//        val seconds = UtilityMethods.calculateInterval(chartData.options.intervalUpdateIndex)
//        Logger.i("Seconds is $seconds")
//        val notificationWork = PeriodicWorkRequestBuilder<NotificationWorker>(seconds, TimeUnit.SECONDS)
//                .setConstraints(myConstraints)
//                .setInputData(inputData)
//                .build()
//
//        context?.let {
//            WorkManager.getInstance(it).enqueueUniquePeriodicWork(UtilityMethods.generateChannelId(chartData), ExistingPeriodicWorkPolicy.KEEP, notificationWork)
//        }
//    }
//
//    override fun dequeueWorker(chartData: ChartData) {
//        context?.let {
//            WorkManager.getInstance(it).cancelUniqueWork(UtilityMethods.generateChannelId(chartData))
//        }
//    }


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
                .backgroundColor(R.color.colorShowCaseBG)
                .build()

            showCaseQueue = FancyShowCaseQueue()
                .add(showCase1)

            showCaseQueue.show()
            Hawk.put(Constants.HAWK_SHOWCASE_0_DONE, true)
        }
    }

}
