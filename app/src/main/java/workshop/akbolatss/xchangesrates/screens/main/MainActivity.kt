package workshop.akbolatss.xchangesrates.screens.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.orhanobut.hawk.Hawk
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import me.yokeyword.fragmentation.SupportActivity
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.screens.charts.ChartFragment
import workshop.akbolatss.xchangesrates.screens.snapshots.SnapshotsFragment
import workshop.akbolatss.xchangesrates.utils.Constants

class MainActivity : SupportActivity() {


    private var mCurrFragPos: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadRootFragment(R.id.container, SnapshotsFragment.newInstance())
        //        loadMultipleRootFragment(R.id.container, 0, SnapshotsFragment.newInstance(), ChartFragment.newInstance());
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_actions, menu)
        val menuTakeSnap = menu.findItem(R.id.action_take_snap)
        val menuRefresh = menu.findItem(R.id.action_refresh)
        val menuNotifications = menu.findItem(R.id.action_notify_options)
        when (mCurrFragPos) {
            0 -> {
                menuTakeSnap.isVisible = false
                menuRefresh.isVisible = true
                menuNotifications.isVisible = true
            }
            1 -> {
                menuTakeSnap.isVisible = true
                menuRefresh.isVisible = true
                menuNotifications.isVisible = false
            }
            else -> {
                menuTakeSnap.isVisible = false
                menuRefresh.isVisible = false
                menuNotifications.isVisible = false
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_take_snap -> {
                findFragment(ChartFragment::class.java).onSaveSnapshot()
                return true
            }
            R.id.action_refresh -> {
                if (mCurrFragPos == 1) {
                    findFragment(ChartFragment::class.java).onUpdate()
                } else if (mCurrFragPos == 0) {
                    val fragment = findFragment(SnapshotsFragment::class.java)
                    fragment?.onUpdateSnapshots()
                }
                return true
            }
            R.id.action_notify_options -> {
                val fragment = findFragment(SnapshotsFragment::class.java)
                fragment?.onNotificationsDialog()
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun onShowCase1() {
        val showCaseQueue: FancyShowCaseQueue

        val menu = findViewById<View>(R.id.action_take_snap)
        val showCase1 = FancyShowCaseView.Builder(this)
                .focusOn(menu)
                .title(resources.getString(R.string.showcase_main_1))
                .backgroundColor(R.color.colorShowCaseBG)
                .build()

        val showCase2 = FancyShowCaseView.Builder(this)
                .title(resources.getString(R.string.showcase_main_2))
                .backgroundColor(R.color.colorShowCaseBG)
                .build()

        showCaseQueue = FancyShowCaseQueue()
                .add(showCase1)
                .add(showCase2)

        showCaseQueue.setCompleteListener { Hawk.put(Constants.HAWK_SHOWCASE_2_DONE, true) }
        showCaseQueue.show()
    }


    fun onShowCase2() {
        val showCaseQueue: FancyShowCaseQueue

        val menu = findViewById<View>(R.id.action_notify_options)
        val showCase1 = FancyShowCaseView.Builder(this)
                .focusOn(menu)
                .title(resources.getString(R.string.showcase_main_3))
                .backgroundColor(R.color.colorShowCaseBG)
                .build()

        val showCase2 = FancyShowCaseView.Builder(this)
                .title(resources.getString(R.string.showcase_main_4))
                .backgroundColor(R.color.colorShowCaseBG)
                .build()

        showCaseQueue = FancyShowCaseQueue()
                .add(showCase1)
                .add(showCase2)

        showCaseQueue.show()
    }
}
