package workshop.akbolatss.xchangesrates.model;

import java.util.List;

public class ChartResponseData {
    private String exchange;
    private String coin;
    private String currency;
    private String source;
    private List<ChartResponseDataChart> chart;
    private ChartResponseDataInfo info;

    public String getExchange() {
        return this.exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<ChartResponseDataChart> getChart() {
        return chart;
    }

    public void setChart(List<ChartResponseDataChart> chart) {
        this.chart = chart;
    }

    public ChartResponseDataInfo getInfo() {
        return this.info;
    }

    public void setInfo(ChartResponseDataInfo info) {
        this.info = info;
    }
}
