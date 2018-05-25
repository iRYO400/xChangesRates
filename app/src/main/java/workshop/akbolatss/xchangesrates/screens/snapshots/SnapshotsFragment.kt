package workshop.akbolatss.xchangesrates.screens.snapshots

import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.fragment_snapshots.*
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import me.yokeyword.fragmentation.SupportFragment
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.app.ApplicationMain
import workshop.akbolatss.xchangesrates.model.dao.Snapshot
import workshop.akbolatss.xchangesrates.model.dao.SnapshotInfo
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository
import workshop.akbolatss.xchangesrates.screens.main.MainActivity
import workshop.akbolatss.xchangesrates.screens.notifications.NotificationsDialogFragment
import workshop.akbolatss.xchangesrates.utils.Constants

class SnapshotsFragment : SupportFragment(), SwipeRefreshLayout.OnRefreshListener, SnapshotsAdapter.onSnapshotClickListener, SnapshotsView, OptionsDialogFragment.OptionsDialogListener {

    internal var mPresenter: SnapshotsPresenter? = null


    private lateinit var mAdapter: SnapshotsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_snapshots, container, false)

        mPresenter = SnapshotsPresenter(DBChartRepository((activity!!.application as ApplicationMain).daoSession,
                ApplicationMain.apiService))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mPresenter != null) {
            mPresenter!!.onViewAttached(this)
        }

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener(this)

        mAdapter = SnapshotsAdapter(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        recyclerView.adapter = mAdapter

        Handler().postDelayed({ mPresenter!!.getAllSnapshots() }, 500)

        if (!Hawk.get(Constants.HAWK_SHOWCASE_0_DONE, false)) {
            val showCaseQueue: FancyShowCaseQueue
            val showCase1 = FancyShowCaseView.Builder(activity!!)
                    .title(resources.getString(R.string.showcase_snap_1))
                    .backgroundColor(R.color.colorShowCaseBG)
                    .build()

            val showCase2 = FancyShowCaseView.Builder(activity!!)
                    .focusOn(fabAdd)
                    .title(resources.getString(R.string.showcase_snap_2))
                    .backgroundColor(R.color.colorShowCaseBG)
                    .build()

            showCaseQueue = FancyShowCaseQueue()
                    .add(showCase1)
                    .add(showCase2)

            showCaseQueue.show()
            Hawk.put(Constants.HAWK_SHOWCASE_0_DONE, true)
        }
    }

    override fun onOpenOptions(chartId: Long, isActive: Boolean, timing: String, pos: Int) {
        val fm = fragmentManager
        val dialogFragment = OptionsDialogFragment.newInstance(chartId, isActive, timing, pos)
        dialogFragment.setTargetFragment(this@SnapshotsFragment, 300)
        dialogFragment.show(fm!!, "fm")
    }

    fun onNotificationsDialog() {
        val fm = fragmentManager
        val dialogFragment = NotificationsDialogFragment.newInstance()
        dialogFragment.setTargetFragment(this@SnapshotsFragment, 400)
        dialogFragment.show(fm!!, "fm")
    }

    override fun onGetInfo(key: Long, pos: Int) {
        mPresenter!!.onLoadInfo(key, pos)
    }

    override fun onLoadSnapshots(snapshotList: List<Snapshot>) {
        mAdapter.onAddItems(snapshotList)

        if (Hawk.get(Constants.HAWK_SHOWCASE_2_DONE)) {
            Handler().postDelayed({
                if (mAdapter.itemCount > 0) {
                    val queue: FancyShowCaseQueue
                    val view = recyclerView.findViewHolderForLayoutPosition(0).itemView
                    val fL = view.findViewById(R.id.frameLayout) as FrameLayout
                    val showCase3 = FancyShowCaseView.Builder(activity!!)
                            .focusOn(fL)
                            .title(resources.getString(R.string.showcase_snap_3))
                            .backgroundColor(R.color.colorShowCaseBG)
                            .build()

                    val showCase4 = FancyShowCaseView.Builder(activity!!)
                            .focusOn(fL)
                            .title(resources.getString(R.string.showcase_snap_4))
                            .backgroundColor(R.color.colorShowCaseBG)
                            .build()

                    queue = FancyShowCaseQueue()
                            .add(showCase3)
                            .add(showCase4)
                    queue.setCompleteListener { (activity as MainActivity).onShowCase2() }

                    queue.show()
                    Hawk.put(Constants.HAWK_SHOWCASE_2_DONE, false)
                }
            }, 500)
        }
    }

    override fun onLoadInfo(dataInfo: SnapshotInfo, pos: Int) {
        mAdapter.onUpdateInfo(dataInfo, pos)
    }

    override fun onLoadChart(data: Snapshot, pos: Int) {
        mAdapter.onUpdateSnapshot(data, pos)
        val view = recyclerView.findViewHolderForLayoutPosition(pos).itemView
        view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
        view.findViewById<TextView>(R.id.tvDate).visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.tvTime).visibility = View.VISIBLE
        view.findViewById<FrameLayout>(R.id.frameLayout).isEnabled = true
        view.findViewById<FrameLayout>(R.id.frameLayout).isClickable = true
    }

    override fun onSaveNotifiesCount(count: Int) {
        Hawk.put(Constants.HAWK_NOTIFIES_COUNT, count)
    }

    override fun onRefresh() {
        mPresenter!!.getAllSnapshots()
    }

    override fun onSaveChanges(chartId: Long, isActive: Boolean, timing: String, pos: Int) {
        mPresenter!!.onUpdateOptions(chartId, isActive, timing)
        mAdapter.onUpdateNotifyState(isActive, timing, pos)
    }

    override fun onRemove(chartId: Long, pos: Int) {
        mPresenter!!.onRemoveSnapshot(chartId)
        mAdapter.onRemoveSnap(pos)
    }

    override fun onUpdateItem(model: Snapshot, pos: Int) {
        mPresenter!!.onUpdateSnapshot(model, pos)
    }

    fun onUpdateSnapshots() {
        if (mAdapter.itemCount <= 0) {
            return
        }
        onShowLoading()
        for (i in 0 until mAdapter.snapshotModels!!.size) {
            mPresenter!!.onUpdateSnapshot(mAdapter.snapshotModels!![i], i)
        }
    }

    override fun onNoContent(isEmpty: Boolean) {
        if (isEmpty) {
            tvNoContent.visibility = View.VISIBLE
        } else {
            tvNoContent.visibility = View.GONE
        }
    }

    override fun onShowLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        fabAdd.visibility = View.GONE
    }

    override fun onHideLoading() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        fabAdd.visibility = View.VISIBLE
        swipeRefresh.isRefreshing = false
    }

    override fun onPause() {
        super.onPause()
        hideSoftInput()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mPresenter != null) {
            mPresenter!!.onViewDetached(this)
        }
    }

    companion object {

        fun newInstance(): SnapshotsFragment {
            return SnapshotsFragment()
        }
    }
}
