package workshop.akbolatss.xchangesrates.model;

import java.util.Map;

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.11.2017
 */

public class CoindeskResponse {

    private TimeResponse timeResponse;

    private String disclaimer;

    private String chartName;

    private Map<String, BPIResponse> bpi;

    public TimeResponse getTimeResponse() {
        return timeResponse;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public String getChartName() {
        return chartName;
    }

    public Map<String, BPIResponse> getBpi() {
        return bpi;
    }
}
