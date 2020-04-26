package workshop.akbolatss.xchangesrates.presentation.root

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceOnClickListener
import kotlinx.android.synthetic.main.activity_main.*
import kz.jgroup.pos.util.EventObserver
import me.yokeyword.fragmentation.SupportActivity
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.presentation.chart.ChartFragment
import workshop.akbolatss.xchangesrates.presentation.snapshots.SnapshotListFragment

class RootActivity : SupportActivity(), SpaceOnClickListener {

    private val viewModel by currentScope.viewModel<RootViewModel>(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSpaceView(savedInstanceState)
        initFragments()
        observeViewModel()
    }

    private fun initFragments() {
        if (findFragment(ScreenState.CHART.fragment) == null
            || findFragment(ScreenState.SNAPSHOTS.fragment) == null
        ) {
            loadMultipleRootFragment(
                R.id.flContainer, 0,
                SnapshotListFragment.newInstance(),
                ChartFragment.newInstance()
            )
        }
    }

    private fun initSpaceView(savedInstanceState: Bundle?) {
        spaceView.initWithSaveInstanceState(savedInstanceState)
        spaceView.addSpaceItem(
            SpaceItem(
                getString(R.string.bottom_bar_snapshots),
                R.drawable.ic_round_insert_chart_outlined_24
            )
        )
        spaceView.addSpaceItem(
            SpaceItem(
                getString(R.string.bottom_bar_charts),
                R.drawable.ic_round_score_24
            )
        )
        spaceView.setSpaceOnClickListener(this)
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(this, EventObserver { state ->
            when (state) {
                ScreenState.SNAPSHOTS -> {
                    val fragment = findFragment(state.fragment)
                    showHideFragment(fragment)
//                    findFragment(SnapshotsFragment::class.java).showItemShowCase()
                    spaceView.changeCenterButtonIcon(R.drawable.ic_round_refresh)
                }
                ScreenState.CHART -> {
                    val fragment = findFragment(state.fragment)
                    showHideFragment(fragment)
//                    findFragment(ChartFragment::class.java).showStartupShowCase()
                    spaceView.changeCenterButtonIcon(R.drawable.ic_round_add)
                }
            }
        })
    }

    override fun onCentreButtonClick() {
        viewModel.screenState.value?.let { event ->
            if (event.peekContent() == ScreenState.CHART)
                findFragment(ChartFragment::class.java).onSaveSnapshot()
            else if (event.peekContent() == ScreenState.SNAPSHOTS)
                findFragment(SnapshotListFragment::class.java).updateAllSnapshots()
        }
    }

    override fun onItemReselected(itemIndex: Int, itemName: String?) = Unit

    override fun onItemClick(itemIndex: Int, itemName: String?) {
        when (itemIndex) {
            0 -> viewModel.showList()
            1 -> viewModel.showCharts()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        currentFocus?.let { currFocus ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(
                currFocus.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
        return super.dispatchTouchEvent(ev)
    }


//    fun onShowCase1() {
//        val showCaseQueue: FancyShowCaseQueue
//
//        val menu = findViewById<View>(R.id.action_take_snap)
//        val showCase1 = FancyShowCaseView.Builder(this)
//                .focusOn(menu)
//                .title(resources.getString(R.string.showcase_main_1))
//                .backgroundColor(R.color.colorShowCaseBG)
//                .build()
//
//        val showCase2 = FancyShowCaseView.Builder(this)
//                .title(resources.getString(R.string.showcase_main_2))
//                .backgroundColor(R.color.colorShowCaseBG)
//                .build()
//
//        showCaseQueue = FancyShowCaseQueue()
//                .add(showCase1)
//                .add(showCase2)
//
//        showCaseQueue.setCompleteListener { Hawk.put(Constants.HAWK_SHOWCASE_2_DONE, true) }
//        showCaseQueue.show()
//    }


//    fun onShowCase2() {
//        val showCaseQueue: FancyShowCaseQueue
//
//        val menu = findViewById<View>(R.id.action_notify_options)
//        val showCase1 = FancyShowCaseView.Builder(this)
//                .focusOn(menu)
//                .title(resources.getString(R.string.showcase_main_3))
//                .backgroundColor(R.color.colorShowCaseBG)
//                .build()
//
//        val showCase2 = FancyShowCaseView.Builder(this)
//                .title(resources.getString(R.string.showcase_main_4))
//                .backgroundColor(R.color.colorShowCaseBG)
//                .build()
//
//        showCaseQueue = FancyShowCaseQueue()
//                .add(showCase1)
//                .add(showCase2)
//
//        showCaseQueue.show()
//    }
}
