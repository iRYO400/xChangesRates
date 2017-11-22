package workshop.akbolatss.xchangesrates.model;

import com.google.gson.annotations.SerializedName;

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.11.2017
 */

public class TimeResponse {

    @SerializedName("updated")
    private String updated;
    @SerializedName("updatedISO")
    private String updatedISO;
    @SerializedName("updateduk")
    private String updateduk;

    public String getUpdated() {
        return updated;
    }

    public String getUpdatedISO() {
        return updatedISO;
    }

    public String getUpdateduk() {
        return updateduk;
    }
}
