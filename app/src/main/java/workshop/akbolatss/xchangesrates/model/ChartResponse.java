package workshop.akbolatss.xchangesrates.model;

public class ChartResponse {
    private String request;
    private int code;
    private ChartResponseData data;
    private String status;

    public String getRequest() {
        return this.request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ChartResponseData getData() {
        return this.data;
    }

    public void setData(ChartResponseData data) {
        this.data = data;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
