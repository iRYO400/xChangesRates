package workshop.akbolatss.xchangesrates.screens.snapshots;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import workshop.akbolatss.xchangesrates.R;

import static workshop.akbolatss.xchangesrates.utils.Constants.BUNDLE_CHART_ID;
import static workshop.akbolatss.xchangesrates.utils.Constants.BUNDLE_ISACTIVE;
import static workshop.akbolatss.xchangesrates.utils.Constants.BUNDLE_POSITION;
import static workshop.akbolatss.xchangesrates.utils.Constants.BUNDLE_TIMING;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_NOTIFIES_COUNT;
import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_1;
import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_12;
import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_24;
import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_3;
import static workshop.akbolatss.xchangesrates.utils.Constants.MINUTES_10;

/**
 * Author: Akbolat Sadvakassov
 * Date: 18.01.2018
 */

public class OptionsDialogFragment extends DialogFragment {

    private final static String TAG = "OptionsDialogFragment";

    @BindView(R.id.switchNotify)
    protected SwitchCompat mSwitchOnOff;

    @BindView(R.id.spinTime)
    protected Spinner mSpinTime;

    @BindView(R.id.tvNotifiesCount)
    protected TextView mTvNotifiesCount;
    private int mNotifiesCount;

    private Unbinder unbinder;

    public OptionsDialogFragment() {
    }

    public static OptionsDialogFragment newInstance(long chartId, boolean isActive, String timing, int pos) {
        OptionsDialogFragment fragment = new OptionsDialogFragment();
        Bundle arg = new Bundle();
        arg.putLong(BUNDLE_CHART_ID, chartId);
        arg.putBoolean(BUNDLE_ISACTIVE, isActive);
        arg.putString(BUNDLE_TIMING, timing);
        arg.putInt(BUNDLE_POSITION, pos);
        fragment.setArguments(arg);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final View view = layoutInflater.inflate(R.layout.fragment_options, null);
        unbinder = ButterKnife.bind(this, view);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        alertDialogBuilder.setView(view);

        View viewTitle = layoutInflater.inflate(R.layout.dialog_options_title, null);
        alertDialogBuilder.setCustomTitle(viewTitle);

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(view.getContext(),
                R.layout.custom_spinner_item, view.getResources().getStringArray(R.array.array_time));
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        mSpinTime.setAdapter(arrayAdapter);

        final boolean isActive = getArguments().getBoolean(BUNDLE_ISACTIVE, false);
        String timing = getArguments().getString(BUNDLE_TIMING, HOUR_24);

        mSwitchOnOff.setChecked(isActive);
        mSpinTime.setSelection(getTimingPos(timing));

        alertDialogBuilder.setPositiveButton(R.string.alert_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OptionsDialogListener mListener = (OptionsDialogListener) getTargetFragment();
                long chartId = getArguments().getLong(BUNDLE_CHART_ID);
                int pos = getArguments().getInt(BUNDLE_POSITION);
                boolean isChecked = mSwitchOnOff.isChecked();
                String timingCode = getTimingCode();
                mListener.onSaveChanges(chartId, isChecked, timingCode, pos);
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialogBuilder.setNeutralButton(R.string.alert_remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OptionsDialogListener mListener = (OptionsDialogListener) getTargetFragment();
                long chartId = getArguments().getLong(BUNDLE_CHART_ID);
                int pos = getArguments().getInt(BUNDLE_POSITION);
                mListener.onRemoveSnapshot(chartId, pos);
            }
        });

        mNotifiesCount = Hawk.get(HAWK_NOTIFIES_COUNT, 0);
        mTvNotifiesCount.setText(mNotifiesCount + getString(R.string.tvNotifiesLimit));

        if (mNotifiesCount >= 5 && !mSwitchOnOff.isChecked()) {
            mSwitchOnOff.setEnabled(false);
        }

        mSwitchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mNotifiesCount++;
                    mTvNotifiesCount.setText(mNotifiesCount + getString(R.string.tvNotifiesLimit));
                } else {
                    mNotifiesCount--;
                    mTvNotifiesCount.setText(mNotifiesCount + getString(R.string.tvNotifiesLimit));
                }
                if (mNotifiesCount >= 5 && !isChecked) {
                    mSwitchOnOff.setEnabled(false);
                } else if (mNotifiesCount >= 5 && isChecked) {
                    mSwitchOnOff.setEnabled(true);
                } else if (mNotifiesCount < 5) {
                    mSwitchOnOff.setEnabled(true);
                }
            }
        });
        return alertDialogBuilder.create();
    }

    public interface OptionsDialogListener {
        void onSaveChanges(long chartId, boolean isActive, String timing, int pos);

        void onRemoveSnapshot(long chartId, int pos);
    }

    private String getTimingCode() {
        switch (mSpinTime.getSelectedItemPosition()) {
            case 0:
                return MINUTES_10;
            case 1:
                return HOUR_1;
            case 2:
                return HOUR_3;
            case 3:
                return HOUR_12;
            case 4:
                return HOUR_24;
            default:
                return HOUR_24;
        }
    }

    private int getTimingPos(String timing) {
        switch (timing) {
            case MINUTES_10:
                return 0;
            case HOUR_1:
                return 1;
            case HOUR_3:
                return 2;
            case HOUR_12:
                return 3;
            case HOUR_24:
                return 4;
            default:
                return 4;
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
