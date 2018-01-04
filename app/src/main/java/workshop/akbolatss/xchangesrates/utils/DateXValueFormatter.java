package workshop.akbolatss.xchangesrates.utils;



import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static workshop.akbolatss.xchangesrates.utils.Constants.DEBUG_TAG;

/**
 * Author: Akbolat Sadvakassov
 * Date: 21.11.2017
 */

public class DateXValueFormatter implements IAxisValueFormatter {

    private DateFormat mDataFormat;
    private Date mDate;

    public DateXValueFormatter() {
        this.mDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        this.mDate = new Date();
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        long convertedTimestamp = (long) value * 1000;
//        Log.d(DEBUG_TAG, "VALUE " + getDate(convertedTimestamp));
        return getDate(convertedTimestamp);
    }

    private String getDate(long timeStamp) {
        try {
            mDate.setTime(timeStamp);
            return mDataFormat.format(mDate);
        } catch (Exception ex) {
            return "xx";
        }
    }
}
