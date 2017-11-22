package workshop.akbolatss.xchangesrates.screens.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import workshop.akbolatss.xchangesrates.R;
import workshop.akbolatss.xchangesrates.app.ApplicationMain;
import workshop.akbolatss.xchangesrates.model.BPIResponse;
import workshop.akbolatss.xchangesrates.model.CoindeskResponse;
import workshop.akbolatss.xchangesrates.utils.DateXValueFormatter;

import static workshop.akbolatss.xchangesrates.utils.Constants.DEBUG_TAG;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_CURRENCY_POS;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_CURRENCY_RATE;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_HISTORY;
import static workshop.akbolatss.xchangesrates.utils.Constants.ONE_MONTH_AGO;
import static workshop.akbolatss.xchangesrates.utils.Constants.THREE_MONTHS_AGO;
import static workshop.akbolatss.xchangesrates.utils.Constants.WEEK_AGO;
import static workshop.akbolatss.xchangesrates.utils.Constants.YEAR_AGO;
import static workshop.akbolatss.xchangesrates.utils.UtilityMethods.getCalculatedBackDays;
import static workshop.akbolatss.xchangesrates.utils.UtilityMethods.getTimestamp;
import static workshop.akbolatss.xchangesrates.utils.UtilityMethods.getTodayDate;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Callback<CoindeskResponse> {

    @BindView(R.id.btnRefresh)
    protected ImageView mBtnUpdate;

    @BindView(R.id.progressBar)
    protected ProgressBar mProgressBar;

    @BindView(R.id.spinCurrencies)
    protected Spinner mSpinCurrencies;

    @BindView(R.id.spinCoin)
    protected Spinner mSpinCoins;

    @BindView(R.id.lineChart)
    protected LineChart mLineChart;

    @BindView(R.id.etCoin)
    protected EditText mEtCoin;

    @BindView(R.id.etCurrency)
    protected EditText mEtCurrency;

    @BindView(R.id.btnWeek)
    protected Button btnWeek;

    @BindView(R.id.btnOneMonth)
    protected Button btnOneMonth;

    @BindView(R.id.btnThreeMonths)
    protected Button btnThreeMonths;

    @BindView(R.id.btnYear)
    protected Button btnYear;

    private Unbinder mUnbinder;

    /**
     * ISO 4170
     */
    private String mSelectedCurrency;
    /**
     * Currency position in Spinner
     */
    private int mSelectedCurrencyPos;
    /**
     * Currency Rate
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);

        onInitSpinner();
        onInitChart();
        onInitValues();
    }

    /**
     * TextWatcher для крипты
     */
    @OnTextChanged(R.id.etCoin)
    protected void onEtCoinChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (isCoinEtFocused && charSequence.length() > 0) {
            float buf = Float.parseFloat(charSequence.toString()) * mSelectedCurrencyRate;
            mEtCurrency.setText(String.valueOf(buf));
        }
    }

    /**
     * TextWatcher для валюты
     */
    @OnTextChanged(R.id.etCurrency)
    protected void onEtCurrencyChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (isCurrencyEtFocused && charSequence.length() > 0) {
            float buf = Float.parseFloat(charSequence.toString()) / mSelectedCurrencyRate;
            mEtCoin.setText(String.valueOf(buf));
        }
    }

    @OnFocusChange({R.id.etCoin, R.id.etCurrency})
    protected void onEtFocusChanged(View view, boolean hasFocus) {
        if (view.getId() == R.id.etCoin) {
            isCoinEtFocused = hasFocus;
            isCurrencyEtFocused = !hasFocus;
        } else if (view.getId() == R.id.etCurrency) {
            isCoinEtFocused = !hasFocus;
            isCurrencyEtFocused = hasFocus;
        }
    }


    private void onInitSpinner() {
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, getResources().getStringArray(R.array.array_currency_codes));
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        mSpinCurrencies.setAdapter(arrayAdapter);
        mSpinCurrencies.setOnItemSelectedListener(this);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, getResources().getStringArray(R.array.array_coin_code));
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        mSpinCoins.setAdapter(arrayAdapter);
    }

    /**
     * Иниц-я Графика (MPA Chart)
     */
    private void onInitChart() {
        mLineChart.getDescription().setEnabled(false);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.setNoDataTextColor(getResources().getColor(R.color.colorSpinTxt));
        mLineChart.setMaxVisibleValueCount(16);

        IAxisValueFormatter xAxisFormatter = new DateXValueFormatter(getTimestamp(getTodayDate()));
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(5);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setTextColor(getResources().getColor(R.color.colorInactive));
        xAxis.setDrawGridLines(false);

        YAxis yAxis = mLineChart.getAxisLeft();
        yAxis.setEnabled(false);

        YAxis yAxis1 = mLineChart.getAxisRight();
        yAxis1.setTextColor(getResources().getColor(R.color.colorInactive));
    }

    /**
     * Выгрузка дефолтных или сохраненных значений
     */
    private void onInitValues() {
        mSelectedCurrencyPos = Hawk.get(HAWK_CURRENCY_POS);
        mSelectedCurrencyRate = Hawk.get(HAWK_CURRENCY_RATE);

        mEtCurrency.setText(String.valueOf(mSelectedCurrencyRate));
        mSpinCurrencies.setSelection(mSelectedCurrencyPos);

        onBtnChangeState(Hawk.get(HAWK_HISTORY, WEEK_AGO));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mSelectedCurrencyPos = i;
        mSelectedCurrency = adapterView.getSelectedItem().toString();
        Hawk.put(HAWK_CURRENCY_POS, mSelectedCurrencyPos);
        onUpdateCurrency();
    }

    /**
     * Обновление курса
     */
    private void onUpdateCurrency() {
        mBtnUpdate.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        ApplicationMain.getAPIService().getCurrencyByCode(mSelectedCurrency)
                .enqueue(new Callback<CoindeskResponse>() {
                    @Override
                    public void onResponse(Call<CoindeskResponse> call, Response<CoindeskResponse> response) {
                        if (response.isSuccessful()) {
                            mSelectedCurrencyRate = response.body().getBpi().get(mSelectedCurrency).getRateFloat();

                            Hawk.put(HAWK_CURRENCY_RATE, mSelectedCurrencyRate);

                            mEtCoin.setText(String.valueOf(1));
                            mEtCurrency.setText(String.valueOf(mSelectedCurrencyRate));

                            mBtnUpdate.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<CoindeskResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, R.string.tvError, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @OnClick(R.id.btnRefresh)
    protected void onRefresh() {
        Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show();
        onUpdateCurrency();
    }

    @OnClick(R.id.btnWeek)
    protected void onLoadLastWeek() {
        onBtnChangeState(WEEK_AGO);
    }

    @OnClick(R.id.btnOneMonth)
    protected void onLoadOneMonth() {
        onBtnChangeState(ONE_MONTH_AGO);
    }

    @OnClick(R.id.btnThreeMonths)
    protected void onLoadThreeMonths() {
        onBtnChangeState(THREE_MONTHS_AGO);
    }

    @OnClick(R.id.btnYear)
    protected void onLoadYear() {
        onBtnChangeState(YEAR_AGO);
    }

    private void onBtnChangeState(int btnId) {
        switch (btnId) {
            case WEEK_AGO:
                Hawk.put(HAWK_HISTORY, WEEK_AGO);
                btnWeek.setSelected(true);
                btnOneMonth.setSelected(false);
                btnThreeMonths.setSelected(false);
                btnYear.setSelected(false);
                ApplicationMain.getAPIService().getHistoricalByCode(mSpinCurrencies.getSelectedItem().toString(), getTodayDate(), getCalculatedBackDays(WEEK_AGO))
                        .enqueue(this);
                break;
            case ONE_MONTH_AGO:
                Hawk.put(HAWK_HISTORY, ONE_MONTH_AGO);
                btnWeek.setSelected(false);
                btnOneMonth.setSelected(true);
                btnThreeMonths.setSelected(false);
                btnYear.setSelected(false);
                ApplicationMain.getAPIService().getHistoricalByCode(mSpinCurrencies.getSelectedItem().toString(), getTodayDate(), getCalculatedBackDays(ONE_MONTH_AGO))
                        .enqueue(this);
                break;
            case THREE_MONTHS_AGO:
                Hawk.put(HAWK_HISTORY, THREE_MONTHS_AGO);
                btnWeek.setSelected(false);
                btnOneMonth.setSelected(false);
                btnThreeMonths.setSelected(true);
                btnYear.setSelected(false);
                ApplicationMain.getAPIService().getHistoricalByCode(mSpinCurrencies.getSelectedItem().toString(), getTodayDate(), getCalculatedBackDays(THREE_MONTHS_AGO))
                        .enqueue(this);
                break;
            case YEAR_AGO:
                Hawk.put(HAWK_HISTORY, YEAR_AGO);
                btnWeek.setSelected(false);
                btnOneMonth.setSelected(false);
                btnThreeMonths.setSelected(false);
                btnYear.setSelected(true);
                ApplicationMain.getAPIService().getHistoricalByCode(mSpinCurrencies.getSelectedItem().toString(), getTodayDate(), getCalculatedBackDays(YEAR_AGO))
                        .enqueue(this);
                break;
        }
    }

    /**
     * Response на данные для графиков
     *
     * @param call
     * @param response
     */
    @Override
    public void onResponse(Call<CoindeskResponse> call, Response<CoindeskResponse> response) {
        if (response.isSuccessful()) {
            List<Entry> entries = new ArrayList<>();

            for (Map.Entry<String, BPIResponse> cursor : response.body().getBpi().entrySet()) {
                entries.add(new BarEntry(getTimestamp(cursor.getKey()) - getTimestamp(getTodayDate()), cursor.getValue().getRateFloat()));
            }

            LineDataSet set = new LineDataSet(entries, "");
            set.setDrawFilled(true);
            set.setDrawCircles(true);
            set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set.setCubicIntensity(0.2f);
            set.setLineWidth(1.8f);
            set.setColor(getResources().getColor(R.color.colorAccent));
            set.setCircleRadius(2f);
            set.setCircleColor(Color.WHITE);
            set.setValueTextColor(Color.WHITE);
            set.setFillColor(getResources().getColor(R.color.colorPrimaryDark));
            set.setFillAlpha(100);

            LineData lineData = new LineData(set);
            lineData.setValueTextSize(9f);
            lineData.setHighlightEnabled(false);

            mLineChart.setData(lineData);
            mLineChart.invalidate();
        }
    }

    @Override
    public void onFailure(Call<CoindeskResponse> call, Throwable t) {
        Toast.makeText(this, R.string.tvError, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }
}
