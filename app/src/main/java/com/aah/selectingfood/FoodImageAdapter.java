package com.aah.selectingfood;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by sebas on 28.04.2017.
 */

public class FoodImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<Food> mFoods;

    public FoodImageAdapter(Context c, List<Food> foods) {
        mContext = c;
        mFoods = foods;
    }

    public int getCount() {
        return mFoods.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(100, 150));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mFoods.get(position).getmImageId());
        return imageView;
    }
}