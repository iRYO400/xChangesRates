package workshop.akbolatss.xchangesrates.screens.charts;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragment;
import workshop.akbolatss.xchangesrates.R;
import workshop.akbolatss.xchangesrates.app.ApplicationMain;
import workshop.akbolatss.xchangesrates.model.dao.DaoMaster;
import workshop.akbolatss.xchangesrates.model.dao.DaoSession;
import workshop.akbolatss.xchangesrates.model.response.ChartResponseData;
import workshop.akbolatss.xchangesrates.model.response.ExchangeResponse;
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository;
import workshop.akbolatss.xchangesrates.utils.DateXValueFormatter;

import static workshop.akbolatss.xchangesrates.utils.Constants.DB_SNAPS_NAME;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_EXCHANGE_RESPONSE;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_HISTORY_CODE;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_HISTORY_POS;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_XCHANGE_POS;
import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_1;
import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_12;
import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_24;
import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_3;
import static workshop.akbolatss.xchangesrates.utils.Constants.MINUTES_10;
import static workshop.akbolatss.xchangesrates.utils.Constants.MONTH;
import static workshop.akbolatss.xchangesrates.utils.Constants.MONTH_3;
import static workshop.akbolatss.xchangesrates.utils.Constants.MONTH_6;
import static workshop.akbolatss.xchangesrates.utils.Constants.WEEK;
import static workshop.akbolatss.xchangesrates.utils.Constants.YEAR_1;
import static workshop.akbolatss.xchangesrates.utils.Constants.YEAR_2;
import static workshop.akbolatss.xchangesrates.utils.Constants.YEAR_5;

public class ChartFragment extends SupportFragment implements AdapterView.OnItemSelectedListener,
        HorizontalBtnsAdapter.onBtnClickListener, ChartView, ISupportFragment {

    private ChartPresenter mPresenter;
    private Context mContext;
    private onChartFragmentListener mListener;

    @BindView(R.id.spinCurrencies)
    protected Spinner mSpinCurrencies;

    @BindView(R.id.spinCoin)
    protected Spinner mSpinCoins;

    @BindView(R.id.spinExchanges)
    protected Spinner mSpinXchanges;

    @BindView(R.id.lineChart)
    protected LineChart mLineChart;

    @BindView(R.id.etCoin)
    protected EditText mEtCoin;

    @BindView(R.id.etCurrency)
    protected EditText mEtCurrency;

    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerV;
    private HorizontalBtnsAdapter mBtnsAdapter;

    private ExchangeResponse mExchangeResponse;
    /**
     * Коэффициент курса выбранной валюты
     */
    private float mSelectedCurrencyRate;
    /**
     * Фокус на mEtCoin
     */
    private boolean isCoinEtFocused;
    /**
     * Фокус на mEtCurrency
     */
    private boolean isCurrencyEtFocused;

    public static ChartFragment newInstance() {
        return new ChartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        mContext = container.getContext();

        mPresenter = new ChartPresenter(new DBChartRepository(provideDaoSession(container.getContext()),
                ApplicationMain.getAPIService()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPresenter != null) {
            mPresenter.onViewAttached(this);
        }
        onInitRV();
        onInitChart();
        onInitSpinner();
    }

    private DaoSession provideDaoSession(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_SNAPS_NAME);
        Database db = helper.getWritableDb();
        return new DaoMaster(db).newSession();
    }

    private void onInitSpinner() {
        mExchangeResponse = Hawk.get(HAWK_EXCHANGE_RESPONSE);

        String[] ids = new String[mExchangeResponse.getData().size()];
        for (int i = 0; i < mExchangeResponse.getData().size(); i++) {
            ids[i] = mExchangeResponse.getData().get(i).getCaption();
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(mContext, R.layout.custom_spinner_item, ids);
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        mSpinXchanges.setAdapter(arrayAdapter);
        mSpinXchanges.setOnItemSelectedListener(ChartFragment.this);
        mSpinCoins.setOnItemSelectedListener(ChartFragment.this); // Use in this order!
        mSpinCurrencies.setOnItemSelectedListener(ChartFragment.this); // !!!
    }

    private void onInitChart() {
        mLineChart.getDescription().setEnabled(false);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.setNoDataTextColor(getResources().getColor(R.color.colorSpinTxt));
        mLineChart.setMaxVisibleValueCount(12);

        IAxisValueFormatter xAxisFormatter = new DateXValueFormatter();
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(5);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setTextColor(getResources().getColor(R.color.colorInactive));
        xAxis.setDrawGridLines(false);

        YAxis yAxis = mLineChart.getAxisLeft();
        yAxis.setTextColor(getResources().getColor(R.color.colorInactive));

        YAxis yAxis1 = mLineChart.getAxisRight();
        yAxis1.setEnabled(false);
    }

    private void onInitRV() {
        List<String> strings = new ArrayList<>();
        strings.add(MINUTES_10);
        strings.add(HOUR_1);
        strings.add(HOUR_3);
        strings.add(HOUR_12);
        strings.add(HOUR_24);
        strings.add(WEEK);
        strings.add(MONTH);
        strings.add(MONTH_3);
        strings.add(MONTH_6);
        strings.add(YEAR_1);
        strings.add(YEAR_2);
        strings.add(YEAR_5);

        int selectedHistory = Hawk.get(HAWK_HISTORY_POS);
        mPresenter.setTerm(Hawk.get(HAWK_HISTORY_CODE, HOUR_24));

        mRecyclerV.setHasFixedSize(true);
        mRecyclerV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        mBtnsAdapter = new HorizontalBtnsAdapter(strings, selectedHistory, this);
        mRecyclerV.setAdapter(mBtnsAdapter);
        mRecyclerV.smoothScrollToPosition(selectedHistory);
    }

    /**
     * TextWatcher для крипты
     */
    @OnTextChanged(R.id.etCoin)
    protected void onEtCoinChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (isCoinEtFocused && charSequence.length() > 0) {
            try {
                float buf = Float.parseFloat(charSequence.toString().trim()) * mSelectedCurrencyRate;
                mEtCurrency.setText(String.valueOf(buf));
            } catch (NumberFormatException e) {
//                e.printStackTrace();
            }
        }
    }

    /**
     * TextWatcher для валюты
     */
    @OnTextChanged(R.id.etCurrency)
    protected void onEtCurrencyChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (isCurrencyEtFocused && charSequence.length() > 0) {
            try {
                float buf = Float.parseFloat(charSequence.toString().trim()) / mSelectedCurrencyRate;
                mEtCoin.setText(String.valueOf(buf));
            } catch (NumberFormatException e) {
//                e.printStackTrace();
            }
        }
    }

    @OnFocusChange({R.id.etCoin, R.id.etCurrency, R.id.spinExchanges, R.id.spinCoin, R.id.spinCurrencies})
    protected void onEtFocusChanged(View view, boolean hasFocus) {
        if (view.getId() == R.id.etCoin) {
            isCoinEtFocused = hasFocus;
            isCurrencyEtFocused = !hasFocus;
        } else if (view.getId() == R.id.etCurrency) {
            isCoinEtFocused = !hasFocus;
            isCurrencyEtFocused = hasFocus;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == mSpinCurrencies.getId()) {
            mPresenter.setCurrencyCode(adapterView.getSelectedItem().toString());
            mPresenter.onUpdate();
        } else if (adapterView.getId() == mSpinCoins.getId()) {
            mPresenter.setCoinCode(adapterView.getSelectedItem().toString());

            List<String> strings = mPresenter.getExchangeModel().getCurrencies().get(mPresenter.getExchangeModel().getCurrencies().keySet().toArray()[i]);
            Collections.sort(strings);

            ArrayAdapter arrayAdapter = new ArrayAdapter<>(adapterView.getContext(), R.layout.custom_spinner_item, strings);
            arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
            mSpinCurrencies.setAdapter(arrayAdapter);
        } else if (adapterView.getId() == mSpinXchanges.getId()) {

            if (Hawk.contains(HAWK_XCHANGE_POS)) {
                mPresenter.setExchangeModel(mExchangeResponse.getData().get(Hawk.get(HAWK_XCHANGE_POS, 0)));
            } else {
                mPresenter.setExchangeModel(mExchangeResponse.getData().get(i));
            }

            Hawk.put(HAWK_XCHANGE_POS, i);

            ArrayAdapter arrayAdapter = new ArrayAdapter<>(adapterView.getContext(), R.layout.custom_spinner_item,
                    mPresenter.getExchangeModel().getCurrencies().keySet().toArray());
            arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
            mSpinCoins.setAdapter(arrayAdapter);
        } else {
            //nothing to prevent double item selection :)
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onLoadLineChart(ChartResponseData chartData) {
        mSelectedCurrencyRate = Float.parseFloat(chartData.getInfo().getLast());

        mEtCoin.setText(String.valueOf(1));
        mEtCurrency.setText(chartData.getInfo().getLast());

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < chartData.getChart().size(); i++) {
            entries.add(new BarEntry(chartData.getChart().get(i).getTimestamp(),
                    Float.parseFloat(chartData.getChart().get(i).getPrice())));
        }

        LineDataSet set = new LineDataSet(entries, "");
        set.setDrawFilled(true);
        set.setDrawCircles(true);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        set.setLineWidth(1.8f);
        set.setColor(mContext.getResources().getColor(R.color.colorAccent));
        set.setCircleRadius(2f);
        set.setCircleColor(Color.WHITE);
        set.setValueTextColor(Color.WHITE);
        set.setFillColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        set.setFillAlpha(100);

        LineData lineData = new LineData(set);
        lineData.setValueTextSize(9f);
        lineData.setHighlightEnabled(false);

        mLineChart.setData(lineData);
        mLineChart.invalidate();
    }

    @Override
    public void onBtnClick(String id, int pos) {
        mPresenter.setTerm(id);
        mPresenter.onUpdate();
        Hawk.put(HAWK_HISTORY_CODE, id);
        Hawk.put(HAWK_HISTORY_POS, pos);
    }

    public void onUpdate() {
        mPresenter.onUpdate();
    }

    public void onSaveSnapshot() {
        Toast.makeText(mContext, R.string.toast_saving, Toast.LENGTH_SHORT).show();
        mPresenter.onSaveSnap();
    }

    @Override
    public void onShowLoading() {
        mListener.onShowLoading();
    }

    @Override
    public void onHideLoading() {
        mListener.onHideLoading();
    }

    @Override
    public void onNoContent(boolean isEmpty) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onChartFragmentListener) {
            mListener = (onChartFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onChartFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.onViewDetached(this);
        }
    }

    public interface onChartFragmentListener {
        void onShowLoading();

        void onHideLoading();
    }
}
