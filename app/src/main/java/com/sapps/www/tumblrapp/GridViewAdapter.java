package com.sapps.www.tumblrapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

/**
 * Created by Hoang on 8/12/2014.
 */
public class GridViewAdapter extends ArrayAdapter<GalleryItem> {

    private Context mContext;

    public GridViewAdapter(ArrayList<GalleryItem> items, Context context) {
        super(context, R.layout.gallery_item, items);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.gallery_item, parent, false);
        }

        GalleryItem item = getItem(position);
        String mUrl = item.getSmallUrl();

        ImageView imageView = (ImageView)
                convertView.findViewById(R.id.gallery_item_imageView);

        Ion.with(imageView).load(mUrl);

        return convertView;
    }
}
