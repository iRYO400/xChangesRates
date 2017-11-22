package workshop.akbolatss.xchangesrates.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.11.2017
 */

public class BPIResponse {

    private String code;
    private String symbol;
    private String rate;
    private String description;
    @SerializedName("rate_float")
    private float rateFloat;

    public BPIResponse(float rateFloat) {
        this.rateFloat = rateFloat;
    }

    public String getCode() {
        return code;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getRate() {
        return rate;
    }

    public String getDescription() {
        return description;
    }

    public float getRateFloat() {
        return rateFloat;
    }
}
