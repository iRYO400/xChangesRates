package workshop.akbolatss.xchangesrates.screens.snapshots;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportFragment;
import workshop.akbolatss.xchangesrates.R;
import workshop.akbolatss.xchangesrates.app.ApplicationMain;
import workshop.akbolatss.xchangesrates.model.dao.ChartData;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataInfo;
import workshop.akbolatss.xchangesrates.model.dao.DaoMaster;
import workshop.akbolatss.xchangesrates.model.dao.DaoSession;
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository;
import workshop.akbolatss.xchangesrates.screens.notifications.NotificationsDialogFragment;

import static workshop.akbolatss.xchangesrates.utils.Constants.DB_SNAPS_NAME;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_NOTIFIES_COUNT;

public class SnapshotsFragment extends SupportFragment implements SwipeRefreshLayout.OnRefreshListener,
        SnapshotsAdapter.onSnapshotClickListener, SnapshotsView, OptionsDialogFragment.OptionsDialogListener {

    private SnapshotsPresenter mPresenter;

    @BindView(R.id.progressBar)
    protected ProgressBar mProgress;

    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerV;
    private SnapshotsAdapter mAdapter;

    @BindView(R.id.tvNoContent)
    protected TextView mTvNoContent;

    @BindView(R.id.swipeRefresh)
    protected SwipeRefreshLayout mRefreshLayout;

    public static SnapshotsFragment newInstance() {
        return new SnapshotsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_snapshots, container, false);
        ButterKnife.bind(this, view);

        mPresenter = new SnapshotsPresenter(new DBChartRepository(provideDaoSession(container.getContext()),
                ApplicationMain.getAPIService()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPresenter != null) {
            mPresenter.onViewAttached(this);
        }

        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(this);

        mAdapter = new SnapshotsAdapter(this);
        mRecyclerV.setHasFixedSize(true);
        mRecyclerV.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerV.setAdapter(mAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.getAllSnapshots();
            }
        }, 500);
    }

    private DaoSession provideDaoSession(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_SNAPS_NAME);
        Database db = helper.getWritableDb();
        return new DaoMaster(db).newSession();
    }

    @Override
    public void onUpdateItem(ChartData model, int pos) {
        mPresenter.onUpdateSnapshot(model, pos);
    }

    @Override
    public void onGetInfo(long key, int pos) {
        mPresenter.onLoadInfo(key, pos);
    }

    @Override
    public void onOpenOptions(long chartId, boolean isActive, String timing, int pos) {
        FragmentManager fm = getFragmentManager();
        OptionsDialogFragment dialogFragment = OptionsDialogFragment.newInstance(chartId, isActive, timing, pos);
        dialogFragment.setTargetFragment(SnapshotsFragment.this, 300);
        dialogFragment.show(fm, "fm");
    }

    public void onNotificationsDialog() {
        FragmentManager fm = getFragmentManager();
        NotificationsDialogFragment dialogFragment = NotificationsDialogFragment.newInstance();
        dialogFragment.setTargetFragment(SnapshotsFragment.this, 400);
        dialogFragment.show(fm, "fm");
    }

    @Override
    public void onLoadCharts(List<ChartData> chartDataList) {
        mAdapter.onAddItems(chartDataList);
    }

    @Override
    public void onLoadChartInfo(ChartDataInfo dataInfo, int pos) {
        mAdapter.onUpdateInfo(dataInfo, pos);
    }

    @Override
    public void onLoadChart(ChartData data, int pos) {
        mAdapter.onUpdateSnapshot(data, pos);
    }

    @Override
    public void onSaveNotifiesCount(int count) {
        Hawk.put(HAWK_NOTIFIES_COUNT, count);
    }

    @Override
    public void onRefresh() {
        for (int i = 0; i < mAdapter.getSnapshotModels().size(); i++) {
            mPresenter.onUpdateSnapshot(mAdapter.getSnapshotModels().get(i), i);
        }
    }

    @Override
    public void onNoContent(boolean isEmpty) {
        if (isEmpty) {
            mTvNoContent.setVisibility(View.VISIBLE);
        } else {
            mTvNoContent.setVisibility(View.GONE);
        }
    }

    @Override
    public void onShowLoading() {
        mProgress.setVisibility(View.GONE);
        mRecyclerV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideLoading() {
        mProgress.setVisibility(View.GONE);
        mRecyclerV.setVisibility(View.VISIBLE);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.onViewDetached(this);
        }
    }

    @Override
    public void onSaveChanges(long chartId, boolean isActive, String timing, int pos) {
        mPresenter.onUpdateOptions(chartId, isActive, timing);
        mAdapter.onUpdateNotifyState(isActive, timing, pos);
    }

    @Override
    public void onRemoveSnapshot(long chartId, int pos) {
        mPresenter.onRemoveSnapshot(chartId);
        mAdapter.onRemoveSnap(pos);
    }
}
