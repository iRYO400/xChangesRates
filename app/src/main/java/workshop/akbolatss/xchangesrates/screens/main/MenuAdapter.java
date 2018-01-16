package workshop.akbolatss.xchangesrates.screens.main;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import nl.psdcompany.duonavigationdrawer.views.DuoOptionView;
import workshop.akbolatss.xchangesrates.R;

/**
 * Author: Akbolat Sadvakassov
 * Date: 05.12.2017
 */

public class MenuAdapter extends BaseAdapter {

    private ArrayList<String> mOptions = new ArrayList<>();
    private ArrayList<DuoOptionView> mOptionViews = new ArrayList<>();

    MenuAdapter(ArrayList<String> options) {
        mOptions = options;
    }

    @Override
    public int getCount() {
        return mOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return mOptions.get(position);
    }

    void setViewSelected(int position, boolean selected) {

        // Looping through the options in the menu
        // Selecting the chosen option
        for (int i = 0; i < mOptionViews.size(); i++) {
            if (i == position) {
                mOptionViews.get(i).setSelected(selected);
            } else {
                mOptionViews.get(i).setSelected(!selected);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String option = mOptions.get(position);

        // Using the DuoOptionView to easily recreate the demo
        final DuoOptionView optionView;
        if (convertView == null) {
            optionView = new DuoOptionView(parent.getContext());
        } else {
            optionView = (DuoOptionView) convertView;
        }

        // Using the DuoOptionView's default selectors
        switch (position) {
            case 0:
                optionView.bind(option, parent.getContext().getResources().getDrawable(R.drawable.ic_snapshots), parent.getContext().getDrawable(R.drawable.menu_selected));
                break;
            case 1:
                optionView.bind(option, parent.getContext().getResources().getDrawable(R.drawable.ic_charts), parent.getContext().getDrawable(R.drawable.menu_selected));
                break;
            case 2:
                optionView.bind(option, parent.getContext().getResources().getDrawable(R.drawable.ic_info_alert), parent.getContext().getDrawable(R.drawable.menu_selected));
                break;
            default:
                optionView.bind(option, null, parent.getContext().getDrawable(R.drawable.menu_selected));
        }


        // Adding the views to an array list to handle view selection
        mOptionViews.add(optionView);

        return optionView;
    }

}
