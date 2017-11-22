package workshop.akbolatss.xchangesrates.utils;

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.11.2017
 */

public class Constants {

    public static final String DEBUG_TAG = "DE_TAG";
    public static final String BASE_URL = "https://api.coindesk.com/v1/bpi/";



    public static final String HAWK_FIRST_FRAG = "IsFirstStart";
    public static final String HAWK_CURRENCY_POS = "SelectedCurrency";
    public static final String HAWK_CURRENCY_RATE = "SelectedCurrencyRate";
    public static final String HAWK_HISTORY = "SelectedHistoryPeriod";


    public static final int WEEK_AGO = -7;
    public static final int ONE_MONTH_AGO = -30;
    public static final int THREE_MONTHS_AGO = -90;
    public static final int YEAR_AGO = -365;
}
