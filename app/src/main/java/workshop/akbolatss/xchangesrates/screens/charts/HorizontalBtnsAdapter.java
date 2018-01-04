package workshop.akbolatss.xchangesrates.screens.charts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import workshop.akbolatss.xchangesrates.R;

import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_1;
import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_12;
import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_24;
import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_3;
import static workshop.akbolatss.xchangesrates.utils.Constants.MINUTES_10;
import static workshop.akbolatss.xchangesrates.utils.Constants.MONTH;
import static workshop.akbolatss.xchangesrates.utils.Constants.MONTH_3;
import static workshop.akbolatss.xchangesrates.utils.Constants.MONTH_6;
import static workshop.akbolatss.xchangesrates.utils.Constants.WEEK;
import static workshop.akbolatss.xchangesrates.utils.Constants.YEAR_1;
import static workshop.akbolatss.xchangesrates.utils.Constants.YEAR_2;
import static workshop.akbolatss.xchangesrates.utils.Constants.YEAR_5;

/**
 * Author: Akbolat Sadvakassov
 * Date: 15.12.2017
 */

public class HorizontalBtnsAdapter extends RecyclerView.Adapter<HorizontalBtnsAdapter.HorizontalBtnsVH> {

    private List<String> mBtnIds;
    private int mSelectedPos;
    private final onBtnClickListener mClickListener;
    private List<Button> mBtnsList;

    public HorizontalBtnsAdapter(List<String> mBtnIds, int mSelectedPos, onBtnClickListener mClickListener) {
        this.mBtnIds = mBtnIds;
        this.mSelectedPos = mSelectedPos;
        this.mClickListener = mClickListener;
        this.mBtnsList = new ArrayList<>();
    }

    private final View.OnClickListener mInternalListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String id = view.getTag(R.integer.key_id).toString();
            mSelectedPos = (int) view.getTag(R.integer.key_pos);
            mClickListener.onBtnClick(id, mSelectedPos);

            onUpdateBtns(mSelectedPos);
        }
    };

    @Override
    public HorizontalBtnsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rv_btn, parent, false);
        return new HorizontalBtnsVH(view);
    }

    @Override
    public void onBindViewHolder(HorizontalBtnsVH holder, int position) {
        if (mSelectedPos == position) {
            holder.bind(mBtnIds.get(position),
                    true);
        } else {
            holder.bind(mBtnIds.get(position),
                    false);
        }

        holder.button.setOnClickListener(mInternalListener);
        holder.button.setTag(R.integer.key_id, mBtnIds.get(position));
        holder.button.setTag(R.integer.key_pos, position);
        mBtnsList.add(holder.button);
    }

    public void onUpdateBtns(int selectedPos) {
        for (int i = 0; i < mBtnsList.size(); i++) {
            if (selectedPos == i) {
                mBtnsList.get(i).setSelected(true);
            } else {
                mBtnsList.get(i).setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mBtnIds == null) {
            return 0;
        } else {
            return mBtnIds.size();
        }
    }

    public interface onBtnClickListener {
        public void onBtnClick(String id, int pos);
    }

    public class HorizontalBtnsVH extends RecyclerView.ViewHolder {

        Context context;
        @BindView(R.id.btnRv)
        protected Button button;

        public HorizontalBtnsVH(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }

        public void bind(String s, boolean isSelected) {
            String str = "";
            switch (s) {
                case MINUTES_10:
                    str = context.getResources().getString(R.string.tv10min);
                    break;
                case HOUR_1:
                    str = context.getResources().getString(R.string.tv1h);
                    break;
                case HOUR_3:
                    str = context.getResources().getString(R.string.tv3h);
                    break;
                case HOUR_12:
                    str = context.getResources().getString(R.string.tv12h);
                    break;
                case HOUR_24:
                    str = context.getResources().getString(R.string.tv24h);
                    break;
                case WEEK:
                    str = context.getResources().getString(R.string.tv1w);
                    break;
                case MONTH:
                    str = context.getResources().getString(R.string.tv1m);
                    break;
                case MONTH_3:
                    str = context.getResources().getString(R.string.tv3m);
                    break;
                case MONTH_6:
                    str = context.getResources().getString(R.string.tv6m);
                    break;
                case YEAR_1:
                    str = context.getResources().getString(R.string.tv1y);
                    break;
                case YEAR_2:
                    str = context.getResources().getString(R.string.tv2y);
                    break;
                case YEAR_5:
                    str = context.getResources().getString(R.string.tv5y);
                    break;
            }
            button.setSelected(isSelected);
            button.setText(str);
        }
    }
}
