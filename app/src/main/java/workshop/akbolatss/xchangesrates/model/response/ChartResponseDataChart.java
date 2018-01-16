package workshop.akbolatss.xchangesrates.model.response;

/**
 * Author: Akbolat Sadvakassov
 * Date: 05.01.2018
 */

public class ChartResponseDataChart {
    private float market;
    private String high;
    private String low;
    private String price;
    private long timestamp;

    public float getMarket() {
        return this.market;
    }

    public void setMarket(float market) {
        this.market = market;
    }

    public String getHigh() {
        return this.high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return this.low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
