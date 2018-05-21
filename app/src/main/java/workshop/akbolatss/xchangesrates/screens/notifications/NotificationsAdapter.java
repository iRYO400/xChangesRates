package workshop.akbolatss.xchangesrates.screens.notifications;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import workshop.akbolatss.xchangesrates.R;
import workshop.akbolatss.xchangesrates.model.dao.GlobalNotification;

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.01.2018
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsVH> {

    private List<GlobalNotification> mNotifications;

    private NotificationListener mListener;

    public NotificationsAdapter(NotificationListener mListener) {
        this.mNotifications = new ArrayList<>();
        this.mListener = mListener;
    }

    @Override
    public NotificationsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rv_notification, parent, false);
        return new NotificationsVH(view);
    }

    public void onAddItem(GlobalNotification notification) {
        mNotifications.add(notification);
//        notifyItemInserted(mNotifications.size());
        notifyDataSetChanged();
    }

    public void onAddItems(List<GlobalNotification> list) {
        if (list != null) {
            mNotifications.clear();
            mNotifications.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void onUpdateTime(int hour, int minute, int itemPos) {
        GlobalNotification notification = mNotifications.get(itemPos);
        notification.setHour(hour);
        notification.setMinutes(minute);
        notification.buildName();
        mNotifications.set(itemPos, notification);
        notifyItemChanged(itemPos);
//        notifyDataSetChanged();
//        Log.d("TAG", "onUpdateTime");
    }

    public void onUpdateState(boolean isActive, int itemPos) {
        GlobalNotification notification = mNotifications.get(itemPos);
        notification.setIsActive(isActive);
        mNotifications.set(itemPos, notification);
//        notifyItemChanged(itemPos);
//        notifyDataSetChanged();
    }

    public void onRemoveTime(int itemPos) {
        mNotifications.remove(itemPos);
//        notifyItemRemoved(itemPos);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(NotificationsVH holder, int position) {
        holder.bind(mNotifications.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        if (mNotifications == null) {
            return 0;
        } else {
            return mNotifications.size();
        }
    }

    public List<GlobalNotification> getNotifications() {
        return mNotifications;
    }

    public interface NotificationListener {
        void onChangeTime(int pos);

        void onStateChanged(int pos, boolean isActive);

        void onRemoveTime(int pos);
    }

    public class NotificationsVH extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTiming)
        TextView timing;
        @BindView(R.id.flTiming)
        FrameLayout frameLayout;
        @BindView(R.id.cbNotification)
        CheckBox checkBox;

        public NotificationsVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(GlobalNotification notification, final NotificationListener listener) {

            timing.setText(notification.getName());
            checkBox.setChecked(notification.getIsActive());

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onUpdateState(isChecked, getAdapterPosition());
                }
            });

            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onChangeTime(getAdapterPosition());
                }
            });

//            frameLayout.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    listener.onRemoveTime(getAdapterPosition());
//                    return false;
//                }
//            });
        }
    }
}
