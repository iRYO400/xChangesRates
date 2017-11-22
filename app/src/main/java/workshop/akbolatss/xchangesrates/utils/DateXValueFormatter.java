package workshop.akbolatss.xchangesrates.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Author: Akbolat Sadvakassov
 * Date: 21.11.2017
 */

public class DateXValueFormatter implements IAxisValueFormatter {

    private long mReferenceTimestamp; // minimum timestamp in your data set
    private DateFormat mDataFormat;
    private Date mDate;

    public DateXValueFormatter(long mReferenceTimestamp) {
        this.mReferenceTimestamp = mReferenceTimestamp;
        this.mDataFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        this.mDate = new Date();
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        long convertedTimestamp = (long) value;

        long originalTimestamp = mReferenceTimestamp + convertedTimestamp;

        return getDate(originalTimestamp);
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
