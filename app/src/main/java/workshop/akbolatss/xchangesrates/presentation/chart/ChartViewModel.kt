package workshop.akbolatss.xchangesrates.presentation.chart

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.BaseViewModel
import workshop.akbolatss.xchangesrates.data.persistent.model.Chart
import workshop.akbolatss.xchangesrates.data.persistent.model.Exchange
import workshop.akbolatss.xchangesrates.domain.usecase.LoadChartUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.LoadExchangesUseCase
import workshop.akbolatss.xchangesrates.presentation.model.ChartPeriod
import workshop.akbolatss.xchangesrates.utils.Constants.HOUR_1
import workshop.akbolatss.xchangesrates.utils.Constants.HOUR_12
import workshop.akbolatss.xchangesrates.utils.Constants.HOUR_24
import workshop.akbolatss.xchangesrates.utils.Constants.HOUR_3
import workshop.akbolatss.xchangesrates.utils.Constants.MINUTES_10
import workshop.akbolatss.xchangesrates.utils.Constants.MONTH
import workshop.akbolatss.xchangesrates.utils.Constants.MONTH_3
import workshop.akbolatss.xchangesrates.utils.Constants.MONTH_6
import workshop.akbolatss.xchangesrates.utils.Constants.WEEK
import workshop.akbolatss.xchangesrates.utils.Constants.YEAR_1
import workshop.akbolatss.xchangesrates.utils.Constants.YEAR_2
import workshop.akbolatss.xchangesrates.utils.Constants.YEAR_5

class ChartViewModel(
    private val findExchangesUseCase: LoadExchangesUseCase,
    private val loadChartUseCase: LoadChartUseCase
) : BaseViewModel() {

    val exchangeList = MutableLiveData<List<Exchange>>()
    val exchange = MediatorLiveData<Exchange>()
    val exchangeError = MutableLiveData<Int>()

    val coinList = MediatorLiveData<List<String>>()
    val coin = MutableLiveData<String>()
    val coinError = MutableLiveData<Int>()

    val currencyList = MediatorLiveData<List<String>>()
    val currency = MutableLiveData<String>()
    val currencyError = MutableLiveData<Int>()

    val chart = MutableLiveData<Chart>()

    val chartPeriodList = MutableLiveData<List<ChartPeriod>>()
    val selectedPeriod = MutableLiveData<ChartPeriod>()
    var selectedPeriodPreviousPosition = -1
    var selectedPeriodPosition = -1

    init {
        setObservers()
        initChartPeriod()
        loadAll()
    }

    private fun setObservers() {
        exchange.addSource(exchangeList) { exchanges ->
            if (!exchanges.isNullOrEmpty()) {
                exchange.value = exchanges.first()
            }
        }
        coinList.addSource(exchange) { exchange ->
            loadCoinsWithDefault(exchange)
        }
        currencyList.addSource(coin) { coin ->
            loadCurrenciesWithDefault(coin)
            tryLoadChart()
        }
    }

    private fun initChartPeriod() {
        val chartPeriods = arrayListOf(
            ChartPeriod(R.string.tv10min, MINUTES_10, false),
            ChartPeriod(R.string.tv1h, HOUR_1, false),
            ChartPeriod(R.string.tv3h, HOUR_3, false),
            ChartPeriod(R.string.tv12h, HOUR_12, false),
            ChartPeriod(R.string.tv24h, HOUR_24, false),
            ChartPeriod(R.string.tv1w, WEEK, false),
            ChartPeriod(R.string.tv1m, MONTH, false),
            ChartPeriod(R.string.tv3m, MONTH_3, false),
            ChartPeriod(R.string.tv6m, MONTH_6, false),
            ChartPeriod(R.string.tv1y, YEAR_1, false),
            ChartPeriod(R.string.tv2y, YEAR_2, false),
            ChartPeriod(R.string.tv5y, YEAR_5, false)
        )
        chartPeriodList.value = chartPeriods
        selectedPeriod.value = chartPeriods.first()
    }

    private fun loadCoinsWithDefault(exchange: Exchange) {
        if (exchange.currencies.isNotEmpty()) {
            val coins = exchange.currencies.keys.toList().sorted()
            coinList.postValue(coins)

            if (coins.isNotEmpty())
                coin.value = coins.first()
        }
    }

    private fun loadCurrenciesWithDefault(coin: String) {
        exchange.value?.let { exchange ->
            val currencies =
                exchange.currencies[coin]?.sorted()
            currencyList.postValue(currencies)

            if (!currencies.isNullOrEmpty())
                currency.value = currencies.first()
        }
    }

    private fun loadAll() {
        launchOperation(operation = { scope ->
            findExchangesUseCase(scope, LoadExchangesUseCase.Params())
        }, success = {
            exchangeList.value = it
        })
    }

    fun tryLoadChart() {
        val exchange = exchange.value
        if (exchange == null) {
            exchangeError.value = R.string.chart_selected_exchange_error
            return
        }
        val coin = coin.value
        if (coin.isNullOrBlank()) {
            coinError.value = R.string.chart_selected_coin_error
            return
        }
        val currency = currency.value
        if (currency.isNullOrBlank()) {
            currencyError.value = R.string.chart_selected_currency_error
            return
        }
        var timing = selectedPeriod.value
        if (timing == null)
            timing =

        Timber.d("tryLoadChart $exchange $coin $currency $timing")

        loadChart(exchange, coin, currency, timing)
    }

    private fun loadChart(
        exchange: Exchange,
        coin: String,
        currency: String,
        timing: String
    ) {
        launchOperation(
            operation = { scope ->
                loadChartUseCase(
                    scope, LoadChartUseCase.Params(
                        exchange = exchange.id,
                        coin = coin,
                        currency = currency,
                        timing = timing
                    )
                )
            }, success = chart::setValue
        )
    }

    fun toggleSelected(chartPeriod: ChartPeriod, position: Int) {
        selectedPeriodPreviousPosition = selectedPeriodPosition

        if (selectedPeriodPosition == position) {
            selectedPeriodPosition = -1
            selectedPeriod.value = null
        } else {
            selectedPeriodPosition = position
            selectedPeriod.value = chartPeriod
        }
    }
}
