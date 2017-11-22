package workshop.akbolatss.xchangesrates.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Author: Akbolat Sadvakassov
 * Date: 22.11.2017
 */

public class UtilityMethods {

    public static long getTimestamp(String bpiDate) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(bpiDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static String getTodayDate() {
        Date date = new Date();
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date);
    }

    public static String getCalculatedBackDays(int daysAgo) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, daysAgo);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(calendar.getTime());
    }
}
