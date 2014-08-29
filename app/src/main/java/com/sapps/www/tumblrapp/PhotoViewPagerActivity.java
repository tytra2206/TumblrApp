package com.sapps.www.tumblrapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import java.util.ArrayList;

/**
 * Created by Hoang on 8/26/2014.
 */
public class PhotoViewPagerActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mAdapter;
    private static final String TAG = "FavoritePhotoViewActivity";
    public static String GALLERY_ITEM_POS = "com.sapps.www.tumblrapp.item_position";
    public static String CALLER_ID = "com.sapps.www.tumblrapp.caller_id";
    private int pos;
    private int callerId;
    private BackgroundFetchr mBackgroundFetchr;
    private ArrayList<GalleryItem> mItems;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fav_photo_view_viewpager);

        mContext = this;
        mBackgroundFetchr = new BackgroundFetchr();
        pos = getIntent().getIntExtra(GALLERY_ITEM_POS, -1);
        callerId = getIntent().getIntExtra(CALLER_ID, -1);
        if(callerId == FavoriteFragment.FAVORITE_PHOTO_ID) {
            mItems = FavoriteGalleryItemLab.get(mContext).getFavoriteGalleryItems();
        } else {
            mItems = SearchGalleryItemLab.get(mContext).getSearchGalleryItems();
        }

        mAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int pos) {
                return PhotoViewFragment.newInstance(pos, callerId);
            }

            @Override
            public int getCount() {
                return mItems.size();
            }
        };

        mViewPager = (ViewPager) findViewById(R.id.favorite_pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(pos);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int pos) {
                GalleryItem item = null;
                if(callerId == FavoriteFragment.FAVORITE_PHOTO_ID) {
                    if(pos >= mItems.size() - 5
                            && mBackgroundFetchr.getStatus() != AsyncTask.Status.RUNNING) {
                        item = mItems.get(0);
                        FavoriteFragment.mOffset += 20;
                        mBackgroundFetchr.execute(item.getBlogName() + ".tumblr.com");
                    }
                } else {
                    if(mBackgroundFetchr.getStatus() != AsyncTask.Status.RUNNING) {
                        mBackgroundFetchr.execute(SearchFragment.mQuery);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private class BackgroundFetchr extends AsyncTask<String, Void, ArrayList<GalleryItem>> {

        @Override
        protected ArrayList<GalleryItem> doInBackground(String... strings) {

            if(callerId == FavoriteFragment.FAVORITE_PHOTO_ID) {
                String url =  "http://api.tumblr.com/v2/blog/" + strings[0] +
                        "/posts/photo?api_key=9BrmFSYl1n5zj72Sb7hW5Rj0GASGNgxqwsdYpglt2fkkiEigxT&offset="
                        + FavoriteFragment.mOffset;

                return TumblrFetchr.parseBlogItems(TumblrFetchr.fetchUrl(url));
            } else {
                String url = "http://api.tumblr.com/v2/tagged?tag=" + strings[0] +
                        "&api_key=9BrmFSYl1n5zj72Sb7hW5Rj0GASGNgxqwsdYpglt2fkkiEigxT&before="
                        + SearchFragment.mBefore;

                return TumblrFetchr.parseTaggedItems(TumblrFetchr.fetchUrl(url));
            }
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            if(galleryItems != null && !galleryItems.isEmpty()) {
                if(callerId == FavoriteFragment.FAVORITE_PHOTO_ID) {
                    FavoriteGalleryItemLab.get(mContext).addFavoriteGalleryItems(galleryItems);
                } else {
                    SearchGalleryItemLab.get(mContext).addSearchGalleryItems(galleryItems);
                }
            }
            mAdapter.notifyDataSetChanged();
            mBackgroundFetchr = new BackgroundFetchr();
        }
    }
}
