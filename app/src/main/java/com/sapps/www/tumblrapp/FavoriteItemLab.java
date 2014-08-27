package com.sapps.www.tumblrapp;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Hoang on 8/14/2014.
 */
public class FavoriteItemLab {

    private static final String TAG = "FavoriteItemLab";
    private static FavoriteItemLab sFavoriteItemLab;
    private Context mContext;
    private ArrayList<FavoriteItem> mItems;
    private JSONSerializer mSerializer;

    private FavoriteItemLab(Context context) {
        mContext = context;
        mSerializer = new JSONSerializer(context, "favorite.json");

        try {
            mItems = mSerializer.loadFavoriteItems();
        }catch (Exception e) {
            Log.e(TAG, "Error loading favorites", e);
            mItems = new ArrayList<FavoriteItem>();
        }

       if(mItems.isEmpty()) {
           FavoriteItem item1 = new FavoriteItem();
           item1.setBlogName("omg-humor");
           item1.setAvatar(item1.getBlogName());
           mItems.add(item1);

           FavoriteItem item2 = new FavoriteItem();
           item2.setBlogName("ipostfun");
           item2.setAvatar(item2.getBlogName());
           mItems.add(item2);

           FavoriteItem item4 = new FavoriteItem();
           item4.setBlogName("comedycentral");
           item4.setAvatar(item4.getBlogName());
           mItems.add(item4);
       }
    }

    public static FavoriteItemLab get(Context context) {
        if (sFavoriteItemLab == null) {
            sFavoriteItemLab = new FavoriteItemLab(context.getApplicationContext());
        }

        return sFavoriteItemLab;
    }

    public ArrayList<FavoriteItem> getFavoriteItems() {
        return mItems;
    }

    public void addFavoriteItem( FavoriteItem item) {
        if(item != null) {
            mItems.add(item);
        }
    }

    public void deleteFavoriteItem(FavoriteItem item) {
        if(item != null) {
            mItems.remove(item);
        }
    }

    public FavoriteItem getFavoriteItem(String blogName) {
        for(FavoriteItem item : mItems) {
            if(item.getBlogName().equals(blogName)) {
                return item;
            }
        }
        return null;
    }

    public boolean saveFavoriteItems() {
        try{
            mSerializer.saveFavoriteItems(mItems);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving favorite items", e);
            return false;
        }
    }
}
