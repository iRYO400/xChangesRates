package workshop.akbolatss.xchangesrates.screens.snapshots;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import workshop.akbolatss.xchangesrates.R;
import workshop.akbolatss.xchangesrates.app.ApplicationMain;
import workshop.akbolatss.xchangesrates.model.ChartResponse;
import workshop.akbolatss.xchangesrates.model.ChartResponseData;

import static workshop.akbolatss.xchangesrates.utils.Constants.DEBUG_TAG;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_SNAPSHOT_LIST;
import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_24;


public class SnapshotsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SnapshotsAdapter.onSnapshotClickListener {

    @BindView(R.id.progressBar)
    protected ProgressBar mProgress;

    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerV;
    private SnapshotsAdapter mAdapter;

    @BindView(R.id.tvNoContent)
    protected TextView mTvNoContent;

    @BindView(R.id.swipeRefresh)
    protected SwipeRefreshLayout mRefreshLayout;

    @NonNull
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public SnapshotsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_snapshots, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setOnRefreshListener(this);

        mAdapter = new SnapshotsAdapter(this);
        mRecyclerV.setHasFixedSize(true);
        mRecyclerV.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerV.setAdapter(mAdapter);


        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                onLoadSnaps();
                mProgress.setVisibility(View.GONE);
                mRecyclerV.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public void onSnapshotClick(final ChartResponseData model, final int pos) {

        mCompositeDisposable.add(ApplicationMain.getAPIService().getSnapshot(
                model.getCoin(),
                model.getExchange(),
                model.getCurrency(),
                HOUR_24
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ChartResponse, ChartResponseData>() {
                    @Override
                    public ChartResponseData apply(ChartResponse chartResponse) throws Exception {
                        return chartResponse.getData();
                    }
                })
                .subscribe(new Consumer<ChartResponseData>() {
                    @Override
                    public void accept(ChartResponseData data) throws Exception {
                        data.setCoin(model.getCoin());
                        onSaveSnap(data, pos);
                        mAdapter.onUpdateItem(data, pos);
                        mRefreshLayout.setRefreshing(false);
                    }
                }));
        Toast.makeText(getContext(), "Hey " + " pos " + pos, Toast.LENGTH_SHORT).show();
    }

    private void onSaveSnap(ChartResponseData model, int pos) {
        List<ChartResponseData> l = Hawk.get(HAWK_SNAPSHOT_LIST);
        l.set(pos, model);
        Hawk.put(HAWK_SNAPSHOT_LIST, l);
    }

    private void onLoadSnaps() {
        if (Hawk.contains(HAWK_SNAPSHOT_LIST)) {
            mTvNoContent.setVisibility(View.GONE);
            List<ChartResponseData> l = Hawk.get(HAWK_SNAPSHOT_LIST);
            mAdapter.onAddItems(l);
        } else {
            mTvNoContent.setVisibility(View.VISIBLE);
        }
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
//        onLoadSnaps();
        for (int i = 0; i < mAdapter.getmSnapshotModels().size(); i++) {

            final int finalI = i;
            Log.d(DEBUG_TAG, "i; " + i + " finalI " + finalI);
            mCompositeDisposable.add(ApplicationMain.getAPIService().getSnapshot(
                    mAdapter.getmSnapshotModels().get(i).getCoin(),
                    mAdapter.getmSnapshotModels().get(i).getExchange(),
                    mAdapter.getmSnapshotModels().get(i).getCurrency(),
                    HOUR_24)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Function<ChartResponse, ChartResponseData>() {
                             @Override
                             public ChartResponseData apply(ChartResponse chartResponse) throws Exception {
                                 return chartResponse.getData();
                             }
                         }
                    ).subscribe(new Consumer<ChartResponseData>() {
                        @Override
                        public void accept(ChartResponseData data) throws Exception {
                            data.setCoin(mAdapter.getmSnapshotModels().get(finalI).getCoin());
                            onSaveSnap(data, finalI);
                            mAdapter.onUpdateItem(data, finalI);
                        }
                    }));
        }
    }


    @Override
    public void onDestroy() {
        // DO NOT CALL .dispose()
        mCompositeDisposable.clear();
        super.onDestroy();
    }
}
