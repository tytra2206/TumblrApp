package com.sapps.www.tumblrapp;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Hoang on 8/27/2014.
 */
public class SearchGalleryItemLab {
    private Context mContext;
    private static  SearchGalleryItemLab searchGalleryItemLab;
    private ArrayList<GalleryItem> mItems;

    private  SearchGalleryItemLab(Context context) {
        mContext = context;
        mItems = new ArrayList<GalleryItem>();
    }

    public static SearchGalleryItemLab get(Context context) {
        if(searchGalleryItemLab == null) {
            searchGalleryItemLab = new  SearchGalleryItemLab(context.getApplicationContext());
        }

        return searchGalleryItemLab;
    }

    public ArrayList<GalleryItem> getSearchGalleryItems() {
        return mItems;
    }

    public void addSearchGalleryItems(ArrayList<GalleryItem> items) {
        mItems.addAll(items);
    }

    public void clearSearchGalleryItems() {
        mItems.clear();
    }
}
