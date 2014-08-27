package com.sapps.www.tumblrapp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Hoang on 8/26/2014.
 */
public class BlogChecker {

    private Context mContext;
    private String mBlogName;
    private ArrayList<FavoriteItem> mItems;

    public BlogChecker(Context context, String blogName) {
        mContext = context;
        mBlogName = blogName;
        mItems = FavoriteItemLab.get(mContext).getFavoriteItems();
    }

    public void checkBlog() {
        new BlogChecking().execute();
    }

    private class BlogChecking extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return TumblrFetchr.isBlogValid(mBlogName);
        }

        @Override
        protected void onPostExecute(Boolean isBlogValid) {
            if(isBlogValid) {
                for (int i = 0; i < mItems.size(); i++) {
                    if (mItems.get(i).getBlogName().equals(mBlogName + ".tumblr.com")) {
                        Toast.makeText(mContext, "Blog already in favorites", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                }
                FavoriteItem item = new FavoriteItem();
                item.setBlogName(mBlogName);
                item.setAvatar(item.getBlogName());
                mItems.add(item);
                Toast.makeText(mContext, "Blog added to favorites", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Blog' s name is invalid", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
