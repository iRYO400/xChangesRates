package workshop.akbolatss.xchangesrates.screens.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.OnCompleteListener;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;
import workshop.akbolatss.xchangesrates.R;
import workshop.akbolatss.xchangesrates.base.LoadingView;
import workshop.akbolatss.xchangesrates.screens.about.AboutFragment;
import workshop.akbolatss.xchangesrates.screens.charts.ChartFragment;
import workshop.akbolatss.xchangesrates.screens.snapshots.SnapshotsFragment;

import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_SHOWCASE_2_DONE;

public class MainActivity extends SupportActivity implements DuoMenuView.OnMenuClickListener, LoadingView, SnapshotsFragment.onSnapshotListener {

    private MenuAdapter mMenuAdapter;

    @BindView(R.id.duoDrawer)
    protected DuoDrawerLayout mDuoDrawer;
    DuoDrawerToggle mDrawerToggle;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.progressBar)
    protected ProgressBar mProgressBar;

    private int mCurrFragPos;

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);

        onInitDrawer();

        loadRootFragment(R.id.container, SnapshotsFragment.newInstance());
//        loadMultipleRootFragment(R.id.container, 0, SnapshotsFragment.newInstance(), ChartFragment.newInstance());
    }

    private void onInitDrawer() {
        setSupportActionBar(mToolbar);

        mDrawerToggle = new DuoDrawerToggle(this,
                mDuoDrawer,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mDuoDrawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mMenuAdapter = new MenuAdapter(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions))));

        DuoMenuView duoMenuView = (DuoMenuView) mDuoDrawer.getMenuView();
        duoMenuView.setOnMenuClickListener(this);
        duoMenuView.setAdapter(mMenuAdapter);

        mMenuAdapter.setViewSelected(0, true);
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onFooterClicked() {
    }

    @Override
    public void onHeaderClicked() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_actions, menu);
        MenuItem menuTakeSnap = menu.findItem(R.id.action_take_snap);
        MenuItem menuRefresh = menu.findItem(R.id.action_refresh);
        MenuItem menuNotifications = menu.findItem(R.id.action_notify_options);
        switch (mCurrFragPos) {
            case 0:
                menuTakeSnap.setVisible(false);
                menuRefresh.setVisible(true);
                menuNotifications.setVisible(true);
                break;
            case 1:
                menuTakeSnap.setVisible(true);
                menuRefresh.setVisible(true);
                menuNotifications.setVisible(false);
                break;
            default:
                menuTakeSnap.setVisible(false);
                menuRefresh.setVisible(false);
                menuNotifications.setVisible(false);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_take_snap:
                findFragment(ChartFragment.class).onSaveSnapshot();
                return true;
            case R.id.action_refresh:
                if (mCurrFragPos == 1) {
                    findFragment(ChartFragment.class).onUpdate();
                } else if (mCurrFragPos == 0) {
                    SnapshotsFragment fragment = findFragment(SnapshotsFragment.class);
                    if (fragment != null) {
                        fragment.onUpdateSnapshots();
                    }
                }
                return true;
            case R.id.action_notify_options:
                SnapshotsFragment fragment = findFragment(SnapshotsFragment.class);
                if (fragment != null) {
                    fragment.onNotificationsDialog();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onShowLoading() {
        mCurrFragPos = 10;
        invalidateOptionsMenu();
        mProgressBar.setVisibility(View.VISIBLE);
        mDuoDrawer.setDrawerLockMode(DuoDrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onHideLoading() {
        mCurrFragPos = 1;
        invalidateOptionsMenu();
        mProgressBar.setVisibility(View.GONE);
        mDuoDrawer.setDrawerLockMode(DuoDrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onNoContent(boolean isEmpty) {
        //Not needed
    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        switch (position) {
            case 0:
                SnapshotsFragment snapshotsFragment = findFragment(SnapshotsFragment.class);
                if (snapshotsFragment == null) {
                    start(SnapshotsFragment.newInstance());
                } else {
                    start(snapshotsFragment, SupportFragment.SINGLETASK);
                }
                mCurrFragPos = 0;
                break;
            case 1:
                ChartFragment chartFragment = findFragment(ChartFragment.class);
                if (chartFragment == null) {
                    popTo(SnapshotsFragment.class, true, new Runnable() {
                        @Override
                        public void run() {
                            loadRootFragment(R.id.container, ChartFragment.newInstance());
                        }
                    });
                } else {
                    start(chartFragment, SupportFragment.SINGLETASK);
                }
                mCurrFragPos = 1;
                break;
            case 2:
                AboutFragment aboutFragment = findFragment(AboutFragment.class);
                if (aboutFragment == null) {
                    start(AboutFragment.newInstance());
                } else {
                    start(aboutFragment, SupportFragment.SINGLETASK);
                }
                mCurrFragPos = 2;
                break;
        }
        mMenuAdapter.setViewSelected(mCurrFragPos, true);
        mDuoDrawer.closeDrawer();

        invalidateOptionsMenu();
    }

    @Override
    public void onOpenChart() {
        mDuoDrawer.openDrawer();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onOptionClicked(1, null);
            }
        }, 500);
    }

    public void onShowCase1() {
        FancyShowCaseQueue showCaseQueue;

        View menu = findViewById(R.id.action_take_snap);
        FancyShowCaseView showCase1 = new FancyShowCaseView.Builder(this)
                .focusOn(menu)
                .title(getResources().getString(R.string.showcase_main_1))
                .backgroundColor(R.color.colorShowCaseBG)
                .build();

        FancyShowCaseView showCase2 = new FancyShowCaseView.Builder(this)
                .title(getResources().getString(R.string.showcase_main_2))
                .backgroundColor(R.color.colorShowCaseBG)
                .build();

        showCaseQueue = new FancyShowCaseQueue()
                .add(showCase1)
                .add(showCase2);

        showCaseQueue.setCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete() {
                Hawk.put(HAWK_SHOWCASE_2_DONE, true);
            }
        });
        showCaseQueue.show();
    }


    public void onShowCase2() {
        FancyShowCaseQueue showCaseQueue;

        View menu = findViewById(R.id.action_notify_options);
        FancyShowCaseView showCase1 = new FancyShowCaseView.Builder(this)
                .focusOn(menu)
                .title(getResources().getString(R.string.showcase_main_3))
                .backgroundColor(R.color.colorShowCaseBG)
                .build();

        FancyShowCaseView showCase2 = new FancyShowCaseView.Builder(this)
                .title(getResources().getString(R.string.showcase_main_4))
                .backgroundColor(R.color.colorShowCaseBG)
                .build();

        showCaseQueue = new FancyShowCaseQueue()
                .add(showCase1)
                .add(showCase2);

        showCaseQueue.show();
    }
}
