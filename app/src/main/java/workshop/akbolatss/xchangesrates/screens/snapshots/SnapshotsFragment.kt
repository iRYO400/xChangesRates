package workshop.akbolatss.xchangesrates.screens.snapshots

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.fragment_snapshots.*
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import me.yokeyword.fragmentation.SupportFragment
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository
import workshop.akbolatss.xchangesrates.screens.notifications.NotificationWorker
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.Constants.WORKER_INPUT_ID
import workshop.akbolatss.xchangesrates.utils.Logger
import workshop.akbolatss.xchangesrates.utils.UtilityMethods
import java.util.concurrent.TimeUnit


class SnapshotsFragment : SupportFragment(),
        SwipeRefreshLayout.OnRefreshListener,
        SnapshotsAdapter.OnSnapshotListener,
        SnapshotsView, OptionsDialogFragment.OptionsDialogListener {

    private lateinit var mPresenter: SnapshotsPresenter

    private lateinit var mAdapter: SnapshotsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_snapshots, container, false)

        mPresenter = SnapshotsPresenter(DBChartRepository(activity!!.applicationContext))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.onViewAttached(this)

        initView()

        Handler().postDelayed({
            mPresenter.getAllSnapshots()

            showStartupShowCase()
        }, 500)
    }

    private fun initView() {
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener(this)

        mAdapter = SnapshotsAdapter(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(_mActivity, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.adapter = mAdapter
    }

    override fun onOpenOptions(chartId: Long, pos: Int) {
        val fm = fragmentManager
        val dialogFragment = OptionsDialogFragment.newInstance(chartId, pos)
        dialogFragment.setTargetFragment(this@SnapshotsFragment, 300)
        dialogFragment.show(fm!!, "fm")
    }

    /**
     * Load all snapshots from DB
     */
    override fun loadChartDatas(chartDataList: List<ChartData>) {
        mAdapter.onAddItems(chartDataList)
    }

    /**
     * Update single snapshot
     */
    override fun onUpdateItem(chartData: ChartData, pos: Int) {
        val view = recyclerView.findViewHolderForAdapterPosition(pos)!!.itemView
        view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        view.findViewById<ImageView>(R.id.imgError).visibility = View.GONE
        view.findViewById<TextView>(R.id.tvTime).visibility = View.INVISIBLE
        view.findViewById<ConstraintLayout>(R.id.snapshotView).isEnabled = false
        view.findViewById<ConstraintLayout>(R.id.snapshotView).isClickable = false
        mPresenter.onUpdateSnapshot(chartData, pos)
    }

    /**
     * Loading to adapter updated single snapshot
     */
    override fun loadChartData(chartData: ChartData, pos: Int) {
        val view = recyclerView.findViewHolderForAdapterPosition(pos)!!.itemView
        view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
        view.findViewById<ImageView>(R.id.imgError).visibility = View.GONE
        view.findViewById<TextView>(R.id.tvTime).visibility = View.VISIBLE
        view.findViewById<ConstraintLayout>(R.id.snapshotView).isEnabled = true
        view.findViewById<ConstraintLayout>(R.id.snapshotView).isClickable = true
        mAdapter.onUpdateSnapshot(chartData, pos)
    }

    /**
     * Error occurred, when updating single snapshot
     */
    override fun onErrorChartItem(pos: Int) {
        val view = recyclerView.findViewHolderForAdapterPosition(pos)!!.itemView
        view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
        view.findViewById<ImageView>(R.id.imgError).visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.tvTime).visibility = View.INVISIBLE
        view.findViewById<ConstraintLayout>(R.id.snapshotView).isEnabled = true
        view.findViewById<ConstraintLayout>(R.id.snapshotView).isClickable = true
    }

    /**
     * SwipeRefresh
     */
    override fun onRefresh() {
        mPresenter.getAllSnapshots()
    }

    /**
     * Save edited changes
     */
    override fun saveSnapshotChanges(chartData: ChartData, lastNotificationState: Boolean, pos: Int) {
        if (lastNotificationState && chartData.isNotificationEnabled) { // Was enabled, then still enabled. Maybe changed timing
            dequeueWorker(chartData)
            UtilityMethods.clearOngoinNotification(chartData, _mActivity)
            enqueueWorker(chartData)
        }
        if (lastNotificationState && !chartData.isNotificationEnabled) { // Was enabled, then disabled, So dequeue it
            dequeueWorker(chartData)
            UtilityMethods.clearOngoinNotification(chartData, _mActivity)
        }
        if (!lastNotificationState && chartData.isNotificationEnabled) { // Was disabled, then enabled. So only enqueue it
            enqueueWorker(chartData)
        }
        if (!lastNotificationState && !chartData.isNotificationEnabled) { // Was disabled, then still disabled. Nothing to do
        }

        mPresenter.onUpdateOptions(chartData, pos)
    }

    override fun enableNotification(chartData: ChartData, pos: Int) {
        val lastState = chartData.isNotificationEnabled
        chartData.isNotificationEnabled = !chartData.isNotificationEnabled
        saveSnapshotChanges(chartData, lastState, pos)
    }

    /**
     * Remove snapshot from database
     */
    override fun removeSnapshot(chartData: ChartData, pos: Int) {
        dequeueWorker(chartData)
        UtilityMethods.clearOngoinNotification(chartData, _mActivity)
        UtilityMethods.deleteNotificationChannel(chartData, _mActivity)
        mAdapter.notifyItemRemoved(pos)
        mPresenter.onRemoveSnapshot(chartData.id)
    }

    override fun toast(s: String) {
        Toast.makeText(_mActivity, s, Toast.LENGTH_LONG).show()
    }

    override fun enqueueWorker(chartData: ChartData) {
        val myConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .build()

        val inputData = Data.Builder()
                .putLong(WORKER_INPUT_ID, chartData.id)
                .build()

        val seconds = UtilityMethods.calculateInterval(chartData.options.intervalUpdateIndex)
        Logger.i("Seconds is $seconds")
        val notificationWork = PeriodicWorkRequestBuilder<NotificationWorker>(seconds, TimeUnit.SECONDS)
                .setConstraints(myConstraints)
                .setInputData(inputData)
                .build()

        context?.let {
            WorkManager.getInstance(it).enqueueUniquePeriodicWork(UtilityMethods.generateChannelId(chartData), ExistingPeriodicWorkPolicy.KEEP, notificationWork)
        }
    }

    override fun dequeueWorker(chartData: ChartData) {
        context?.let {
            WorkManager.getInstance(it).cancelUniqueWork(UtilityMethods.generateChannelId(chartData))
        }
    }


    fun updateAllSnapshots() {
        if (mAdapter.itemCount <= 0) {
            return
        }

        mPresenter.onUpdateAllSnapshots(mAdapter.mList)
    }

    /**
     * No content state
     */
    override fun onNoContent(isEmpty: Boolean) {
        if (isEmpty) {
            tvNoContent.visibility = View.VISIBLE
        } else {
            tvNoContent.visibility = View.GONE
        }
    }

    /**
     * Show loading state
     */
    override fun onShowLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    /**
     * Hide loading state
     */
    override fun onHideLoading() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        swipeRefresh.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.onViewDetached(this)
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

    fun showItemShowCase() {

    }

    override fun onError(message: String) {
        Logger.e("SnapshotFragment: $message")
    }

    companion object {

        fun newInstance(): SnapshotsFragment {
            return SnapshotsFragment()
        }
    }
}
