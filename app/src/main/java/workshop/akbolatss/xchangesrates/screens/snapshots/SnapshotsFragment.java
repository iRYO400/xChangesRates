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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.OnCompleteListener;
import me.yokeyword.fragmentation.SupportFragment;
import workshop.akbolatss.xchangesrates.R;
import workshop.akbolatss.xchangesrates.app.ApplicationMain;
import workshop.akbolatss.xchangesrates.model.dao.ChartData;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataInfo;
import workshop.akbolatss.xchangesrates.model.dao.DaoMaster;
import workshop.akbolatss.xchangesrates.model.dao.DaoSession;
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository;
import workshop.akbolatss.xchangesrates.screens.main.MainActivity;
import workshop.akbolatss.xchangesrates.screens.notifications.NotificationsDialogFragment;

import static workshop.akbolatss.xchangesrates.utils.Constants.DB_SNAPS_NAME;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_NOTIFIES_COUNT;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_SHOWCASE_0_DONE;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_SHOWCASE_2_DONE;

public class SnapshotsFragment extends SupportFragment implements SwipeRefreshLayout.OnRefreshListener,
        SnapshotsAdapter.onSnapshotClickListener, SnapshotsView, OptionsDialogFragment.OptionsDialogListener {

    private SnapshotsPresenter mPresenter;

    private onSnapshotListener mListener;

    @BindView(R.id.progressBar)
    protected ProgressBar mProgress;

    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerV;
    private SnapshotsAdapter mAdapter;

    @BindView(R.id.tvNoContent)
    protected TextView mTvNoContent;

    @BindView(R.id.fabAdd)
    protected FloatingActionButton mFabAdd;

    @BindView(R.id.swipeRefresh)
    protected SwipeRefreshLayout mRefreshLayout;

    private Unbinder unbinder;

    public static SnapshotsFragment newInstance() {
        return new SnapshotsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_snapshots, container, false);
        unbinder = ButterKnife.bind(this, view);

        mPresenter = new SnapshotsPresenter(new DBChartRepository(((ApplicationMain) getActivity().getApplication()).getDaoSession(),
                ApplicationMain.getAPIService()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPresenter != null) {
            mPresenter.onViewAttached(this);
        }

        mRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mRefreshLayout.setOnRefreshListener(this);

        mAdapter = new SnapshotsAdapter(this);
        mRecyclerV.setHasFixedSize(true);
        mRecyclerV.setNestedScrollingEnabled(true);
        mRecyclerV.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerV.setAdapter(mAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.getAllSnapshots();
            }
        }, 500);

        if (!Hawk.get(HAWK_SHOWCASE_0_DONE, false)) {
            FancyShowCaseQueue showCaseQueue;
            FancyShowCaseView showCase1 = new FancyShowCaseView.Builder(getActivity())
                    .title(getResources().getString(R.string.showcase_snap_1))
                    .backgroundColor(R.color.colorShowCaseBG)
                    .build();

            FancyShowCaseView showCase2 = new FancyShowCaseView.Builder(getActivity())
                    .focusOn(mFabAdd)
                    .title(getResources().getString(R.string.showcase_snap_2))
                    .backgroundColor(R.color.colorShowCaseBG)
                    .build();

            showCaseQueue = new FancyShowCaseQueue()
                    .add(showCase1)
                    .add(showCase2);

            showCaseQueue.show();
            Hawk.put(HAWK_SHOWCASE_0_DONE, true);
        }
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
    public void getSnapshots(List<ChartData> chartDataList) {
        mAdapter.onAddItems(chartDataList);

        if (Hawk.get(HAWK_SHOWCASE_2_DONE)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter.getItemCount() > 0) {
                        FancyShowCaseQueue queue;
                        View view1 = mRecyclerV.findViewHolderForLayoutPosition(0).itemView;
                        FancyShowCaseView showCase3 = new FancyShowCaseView.Builder(getActivity())
                                .focusOn(view1.findViewById(R.id.frameLayout))
                                .title(getResources().getString(R.string.showcase_snap_3))
                                .backgroundColor(R.color.colorShowCaseBG)
                                .build();

                        FancyShowCaseView showCase4 = new FancyShowCaseView.Builder(getActivity())
                                .focusOn(view1.findViewById(R.id.frameLayout))
                                .title(getResources().getString(R.string.showcase_snap_4))
                                .backgroundColor(R.color.colorShowCaseBG)
                                .build();

                        queue = new FancyShowCaseQueue()
                                .add(showCase3)
                                .add(showCase4);
                        queue.setCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete() {
                                ((MainActivity) getActivity()).onShowCase2();
                            }
                        });

                        queue.show();
                        Hawk.put(HAWK_SHOWCASE_2_DONE, false);
                    }
                }
            }, 500);
        }
    }

    @Override
    public void onLoadChartInfo(ChartDataInfo dataInfo, int pos) {
        mAdapter.onUpdateInfo(dataInfo, pos);
    }

    @Override
    public void onLoadChart(ChartData data, int pos) {
        mAdapter.onUpdateSnapshot(data, pos);
        View view = mRecyclerV.findViewHolderForLayoutPosition(0).itemView;
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        view.findViewById(R.id.tvDate).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tvTime).setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveNotifiesCount(int count) {
        Hawk.put(HAWK_NOTIFIES_COUNT, count);
    }

    @Override
    public void onRefresh() {
        mPresenter.getAllSnapshots();
    }

    public void onUpdateSnapshots() {
        onShowLoading();
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
        mProgress.setVisibility(View.VISIBLE);
        mRecyclerV.setVisibility(View.GONE);
        mFabAdd.setVisibility(View.GONE);
    }

    @Override
    public void onHideLoading() {
        mProgress.setVisibility(View.GONE);
        mRecyclerV.setVisibility(View.VISIBLE);
        mFabAdd.setVisibility(View.VISIBLE);
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

    @OnClick(R.id.fabAdd)
    protected void onAddSnapshot() {
        mListener.onOpenChart();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onSnapshotListener) {
            mListener = (onSnapshotListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onChartFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        unbinder.unbind();
    }

    public interface onSnapshotListener {
        void onOpenChart();
    }
}
