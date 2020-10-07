package com.example.android2project.model;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.android2project.R;

import java.util.ArrayList;

import nl.psdcompany.duonavigationdrawer.views.DuoOptionView;

public class MenuAdapter extends BaseAdapter {
    private ArrayList<String> mOptions = new ArrayList<>();
    private ArrayList<DuoOptionView> mOptionViews = new ArrayList<>();

    public MenuAdapter(ArrayList<String> options) {
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

    @SuppressLint("UseCompatLoadingForDrawables")
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
        Drawable drawable = null;
        switch (option) {
            case "Feed":
                 drawable = parent.getResources().getDrawable(R.drawable.ic_petclan_logo_24, null);
                break;
            case "Chats":
                drawable = parent.getResources().getDrawable(R.drawable.ic_chat_24, null);
                break;
            case "MarketPlace":
                drawable = parent.getResources().getDrawable(R.drawable.ic_marketplace_24, null);
                break;
            case "Profile":
                drawable = parent.getResources().getDrawable(R.drawable.ic_default_user_pic_24, null);
                break;
            case "Settings":
                drawable = parent.getResources().getDrawable(R.drawable.ic_round_settings_24, null);
                break;
        }
        optionView.bind(option, drawable, null);

        // Adding the views to an array list to handle view selection
        mOptionViews.add(optionView);

        return optionView;
    }
}
