package workshop.akbolatss.xchangesrates.model;

public class ChartResponseDataInfo {
    private float volume;
    private String high;
    private float change24;
    private String last;
    private String low;
    private String buy;
    private String sell;
    private float change;
    private String started;
    private String multiply;
    private String updated;
    private long timestamp;

    public float getVolume() {
        return this.volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public String getHigh() {
        return this.high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public float getChange24() {
        return this.change24;
    }

    public void setChange24(float change24) {
        this.change24 = change24;
    }

    public String getLast() {
        return this.last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getLow() {
        return this.low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getBuy() {
        return this.buy;
    }

    public void setBuy(String buy) {
        this.buy = buy;
    }

    public String getSell() {
        return this.sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
    }

    public float getChange() {
        return this.change;
    }

    public void setChange(float change) {
        this.change = change;
    }

    public String getStarted() {
        return this.started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getMultiply() {
        return this.multiply;
    }

    public void setMultiply(String multiply) {
        this.multiply = multiply;
    }

    public String getUpdated() {
        return this.updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
