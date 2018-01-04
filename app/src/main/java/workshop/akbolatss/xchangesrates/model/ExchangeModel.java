package workshop.akbolatss.xchangesrates.model;

import java.util.List;
import java.util.Map;

/**
 * Author: Akbolat Sadvakassov
 * Date: 09.12.2017
 */

public class ExchangeModel {
    private String ident;
    private String caption;

    private Map<String, List<String>> currencies;

    public ExchangeModel(String ident, String caption, Map<String, List<String>> currencies) {
        this.ident = ident;
        this.caption = caption;
        this.currencies = currencies;
    }

    public String getIdent() {
        return ident;
    }

    public String getCaption() {
        return caption;
    }

    public Map<String, List<String>> getCurrencies() {
        return currencies;
    }
}
