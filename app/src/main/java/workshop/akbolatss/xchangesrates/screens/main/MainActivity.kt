package workshop.akbolatss.xchangesrates.screens.main

import android.os.Bundle
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceOnClickListener
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_main.*
import me.yokeyword.fragmentation.SupportActivity
import me.yokeyword.fragmentation.SupportFragment
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.screens.charts.ChartFragment
import workshop.akbolatss.xchangesrates.screens.snapshots.SnapshotsFragment
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.UtilityMethods.createDefaultNotificationChannel

const val FIRST = 0
const val SECOND = 1
const val COUNT = 2

class MainActivity : SupportActivity(), SpaceOnClickListener {

    private var mCurrentPosition = 0 //TODO: Save in SaveInstance!!!
    private val mFragments = arrayOfNulls<SupportFragment>(COUNT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firstFragment = findFragment(SnapshotsFragment::class.java)
        if (firstFragment == null) {
            mFragments[FIRST] = SnapshotsFragment.newInstance()
            mFragments[SECOND] = ChartFragment.newInstance()
            loadMultipleRootFragment(R.id.flContainer, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND])
        } else {
            mFragments[FIRST] = firstFragment
            mFragments[SECOND] = findFragment(ChartFragment::class.java)
        }

        initView(savedInstanceState)

        if (!Hawk.contains(Constants.HAWK_CHANNEL_CREATED)) {
            Hawk.put(Constants.HAWK_CHANNEL_CREATED, true)
            createDefaultNotificationChannel(this)
        }
    }


    private fun initView(savedInstanceState: Bundle?) {
        spaceView.initWithSaveInstanceState(savedInstanceState)
        spaceView.addSpaceItem(SpaceItem(getString(R.string.bottom_bar_snapshots), R.drawable.ic_round_insert_chart_outlined_24))
        spaceView.addSpaceItem(SpaceItem(getString(R.string.bottom_bar_charts), R.drawable.ic_round_score_24))
        spaceView.setSpaceOnClickListener(this)
    }

    override fun onCentreButtonClick() {
        if (mCurrentPosition == FIRST) {
            findFragment(SnapshotsFragment::class.java).updateAllSnapshots()
        } else if (mCurrentPosition == SECOND) {
            findFragment(ChartFragment::class.java).onSaveSnapshot()
        }
    }

    override fun onItemReselected(itemIndex: Int, itemName: String?) {
    }

    override fun onItemClick(itemIndex: Int, itemName: String?) {
        when (itemIndex) {
            0 -> {
                showHideFragment(mFragments[FIRST], mFragments[mCurrentPosition])
                spaceView.changeCenterButtonIcon(R.drawable.ic_round_refresh)
                mCurrentPosition = FIRST

                findFragment(SnapshotsFragment::class.java).showItemShowCase()
            }
            1 -> {
                showHideFragment(mFragments[SECOND], mFragments[mCurrentPosition])
                spaceView.changeCenterButtonIcon(R.drawable.ic_round_add)
                mCurrentPosition = SECOND

                findFragment(ChartFragment::class.java).showStartupShowCase()
            }
        }
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
