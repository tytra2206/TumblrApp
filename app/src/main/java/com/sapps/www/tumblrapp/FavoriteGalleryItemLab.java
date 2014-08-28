package com.sapps.www.tumblrapp;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Hoang on 8/27/2014.
 */
public class FavoriteGalleryItemLab {
    private Context mContext;
    private static FavoriteGalleryItemLab favoriteGalleryItemLab;
    private ArrayList<GalleryItem> mItems;

    private FavoriteGalleryItemLab(Context context) {
        mContext = context;
        mItems = new ArrayList<GalleryItem>();
    }

    public static FavoriteGalleryItemLab get(Context context) {
        if(favoriteGalleryItemLab == null) {
            favoriteGalleryItemLab = new FavoriteGalleryItemLab(context.getApplicationContext());
        }

        return favoriteGalleryItemLab;
    }

    public ArrayList<GalleryItem> getFavoriteGalleryItems() {
        return mItems;
    }

    public void addFavoriteGalleryItems(ArrayList<GalleryItem> items) {
        mItems.addAll(items);
    }

    public void clearFavoriteGalleryItems() {
        mItems.clear();
    }
}
