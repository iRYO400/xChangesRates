package workshop.akbolatss.xchangesrates.screens.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;
import workshop.akbolatss.xchangesrates.R;
import workshop.akbolatss.xchangesrates.screens.charts.ChartFragment;
import workshop.akbolatss.xchangesrates.screens.settings.SettingsFragment;
import workshop.akbolatss.xchangesrates.screens.snapshots.SnapshotsFragment;

public class MainActivity extends AppCompatActivity implements DuoMenuView.OnMenuClickListener, ChartFragment.onChartFragmentInteractionListener {

    private MenuAdapter mMenuAdapter;

    @BindView(R.id.duoDrawer)
    protected DuoDrawerLayout mDuoDrawer;

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
    }

    private void onInitDrawer() {
        setSupportActionBar(mToolbar);

        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mDuoDrawer,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mDuoDrawer.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

        mMenuAdapter = new MenuAdapter(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions))));
        mMenuAdapter.setViewSelected(0, true);
        DuoMenuView duoMenuView = (DuoMenuView) mDuoDrawer.getMenuView();
        duoMenuView.setOnMenuClickListener(this);
        duoMenuView.setAdapter(mMenuAdapter);

        onGoToFragment(new ChartFragment(), false);
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

    private void onGoToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(null);
        }

        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_actions, menu);
        MenuItem menuTakeSnap = menu.findItem(R.id.action_take_snap);
        MenuItem menuRefresh = menu.findItem(R.id.action_refresh);
        switch (mCurrFragPos) {
            case 0:
                menuTakeSnap.setVisible(true);
                menuRefresh.setVisible(true);
                break;
            default:
                menuTakeSnap.setVisible(false);
                menuRefresh.setVisible(false);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_take_snap:
                ((ChartFragment) getSupportFragmentManager().findFragmentById(R.id.container)).onTakeSnap();
                return true;
            case R.id.action_refresh:
                ((ChartFragment) getSupportFragmentManager().findFragmentById(R.id.container)).onUpdate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onShowLoading() {
        mCurrFragPos = 10;
        invalidateOptionsMenu();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideLoading() {
        mCurrFragPos = 0;
        invalidateOptionsMenu();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                Toast.makeText(this, "Charts", Toast.LENGTH_SHORT).show();
                fragment = new ChartFragment();
                mCurrFragPos = 0;
                break;
            case 1:
                Toast.makeText(this, "Snapshots", Toast.LENGTH_SHORT).show();
                fragment = new SnapshotsFragment();
                mCurrFragPos = 1;
                break;
            case 2:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                fragment = new SettingsFragment();
                mCurrFragPos = 3;
                break;
            case 3:
                Toast.makeText(this, "About Fragment", Toast.LENGTH_SHORT).show();
                mCurrFragPos = 4;
                break;
        }

        onGoToFragment(fragment, false);

        mDuoDrawer.closeDrawer();

        invalidateOptionsMenu();
    }
}
