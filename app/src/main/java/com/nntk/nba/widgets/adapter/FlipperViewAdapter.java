package com.nntk.nba.widgets.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class FlipperViewAdapter extends BaseAdapter {

    private Context context;

    private int[] imageIds = null;

    public FlipperViewAdapter(Context context, int[] imageIds) {
        this.context = context;
        this.imageIds = imageIds;
    }

    @Override
    public int getCount() {
        return imageIds.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView = null;
        if (convertView == null) {
            imageView = new ImageView(context);
            convertView = imageView;
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(imageIds[position]);

        return imageView;
    }
}
