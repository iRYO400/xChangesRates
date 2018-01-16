package workshop.akbolatss.xchangesrates.model.response;

import java.util.List;

import workshop.akbolatss.xchangesrates.model.ExchangeModel;

/**
 * Author: Akbolat Sadvakassov
 * Date: 08.12.2017
 */

public class ExchangeResponse {

    private String request;
    private List<ExchangeModel> data;

    public String getRequest() {
        return request;
    }

    public List<ExchangeModel> getData() {
        return data;
    }
}
