package workshop.akbolatss.xchangesrates.screens.notifications;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.01.2018
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private boolean isEditing;
    private int mItemPos;

    public TimePickerFragment() {
    }

    public static TimePickerFragment newInstance(boolean isEditing, int itemPos) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle arg = new Bundle();
        arg.putBoolean("isEditing", isEditing);
        arg.putInt("itemPos", itemPos);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        isEditing = getArguments().getBoolean("isEditing", false);
        mItemPos = getArguments().getInt("itemPos", 0);

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public interface TimePickerListener {
        void onAddTime(int hour, int minute);

        void onEditTime(int hour, int minute, int itemPos);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TimePickerListener listener = (TimePickerListener) getTargetFragment();
        if (isEditing) {
            listener.onEditTime(hourOfDay, minute, mItemPos);
        } else {
            listener.onAddTime(hourOfDay, minute);
        }
    }
}
