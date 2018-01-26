package workshop.akbolatss.xchangesrates.screens.notifications;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.greenrobot.greendao.database.Database;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import workshop.akbolatss.xchangesrates.R;
import workshop.akbolatss.xchangesrates.app.ApplicationMain;
import workshop.akbolatss.xchangesrates.model.dao.DaoMaster;
import workshop.akbolatss.xchangesrates.model.dao.DaoSession;
import workshop.akbolatss.xchangesrates.model.dao.Notification;
import workshop.akbolatss.xchangesrates.repositories.DBNotificationRepository;

import static android.content.Context.ALARM_SERVICE;
import static workshop.akbolatss.xchangesrates.utils.Constants.DB_SNAPS_NAME;

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.01.2018
 */

public class NotificationsDialogFragment extends DialogFragment implements NotificationsAdapter.NotificationListener,
        TimePickerFragment.TimePickerListener, DialogInterface.OnClickListener {

    private static final String TAG = "NotifyDialogFragment";

    private Context mContext;

    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerV;
    private NotificationsAdapter mAdapter;

    @BindView(R.id.progressBar)
    protected ProgressBar mProgress;

    @BindView(R.id.llDialog)
    protected LinearLayout mDialogView;

    private DBNotificationRepository mRepository;

    private Unbinder unbinder;

    public NotificationsDialogFragment() {
    }

    public static NotificationsDialogFragment newInstance() {
        NotificationsDialogFragment fragment = new NotificationsDialogFragment();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.fragment_notifications, null);
        unbinder = ButterKnife.bind(this, view);

        mRepository = new DBNotificationRepository(((ApplicationMain) getActivity().getApplication()).getDaoSession());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        alertDialogBuilder.setView(view);

        View viewTitle = layoutInflater.inflate(R.layout.dialog_notifications_title, null);
        alertDialogBuilder.setCustomTitle(viewTitle);

        mAdapter = new NotificationsAdapter(this);
        mRecyclerV.setHasFixedSize(true);
        mRecyclerV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerV.setAdapter(mAdapter);

        mRepository.getAllNotifications()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<List<Notification>>() {
                    @Override
                    public void onSuccess(List<Notification> list) {
                        mAdapter.onAddItems(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });

        alertDialogBuilder.setPositiveButton(R.string.alert_save, this);
        alertDialogBuilder.setNegativeButton(R.string.alert_cancel, this);

        return alertDialogBuilder.create();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.flAddNotify)
    protected void onAddNotify() {
        TimePickerFragment fragment = TimePickerFragment.newInstance(false, 0);
        fragment.setTargetFragment(NotificationsDialogFragment.this, 500);
        fragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onChangeTime(int pos) {
        TimePickerFragment fragment = TimePickerFragment.newInstance(true, pos);
        fragment.setTargetFragment(NotificationsDialogFragment.this, 500);
        fragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onStateChanged(int pos, boolean isActive) {
        mAdapter.onUpdateState(isActive, pos);
    }

    @Override
    public void onRemoveTime(int pos) {
        mAdapter.onRemoveTime(pos);
    }

    @Override
    public void onAddTime(int hour, int minute) {
        Notification notification = new Notification();
        notification.setHour(hour);
        notification.setMinute(minute);
        notification.setIsActive(true);
        notification.buildName();
        mAdapter.onAddItem(notification);
        mRecyclerV.smoothScrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onEditTime(int hour, int minute, int itemPos) {
        mAdapter.onUpdateTime(hour, minute, itemPos);
    }

    @Override
    public void onClick(final DialogInterface dialog, int which) {
        switch (which) {
            case AlertDialog.BUTTON_POSITIVE:
                mProgress.setVisibility(View.VISIBLE);
                mDialogView.setVisibility(View.GONE);
                mRepository.onSaveChanges(mAdapter.getNotifications())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableSingleObserver<List<Notification>>() {
                            @Override
                            public void onSuccess(List<Notification> notificationList) {
                                onActivateNotifications(notificationList);
//                                dialog.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        });
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                dialog.cancel();
                break;
        }
    }

    private void onActivateNotifications(List<Notification> notificationList) {
        for (int i = 0; i < notificationList.size(); i++) {
//            Log.d("TAG", "Notify " + notificationList.get(i).getName() + " " + notificationList.get(i).getId());
            if (notificationList.get(i).getIsActive()) {
                AlarmManager amc = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
                Intent myIntent = new Intent(mContext, NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, notificationList.get(i).getId().intValue(), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, notificationList.get(i).getHour());
                calendar.set(Calendar.MINUTE, notificationList.get(i).getMinute());

                amc.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                Intent intent = new Intent(mContext, NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, notificationList.get(i).getId().intValue(), intent, PendingIntent.FLAG_NO_CREATE);

                if (pendingIntent != null) {
//                    Log.d(TAG, "Not null");
                    AlarmManager amc = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
                    amc.cancel(pendingIntent);
                    pendingIntent.cancel();
                } else {
//                    Log.d(TAG, "Null");
                }
            }
        }
    }
}
