package com.sapps.www.tumblrapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

/**
 * Created by Hoang on 8/26/2014.
 */
public class FavoritePhotoViewActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mAdaper;
    public static String GALLERY_ITEM_POS = "com.sapps.www.tumblrapp.item_position";
    public static String CALLER_ID = "com.sapps.www.tumblrapp.caller_id";
    private int pos;
    private int callerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fav_photo_view_viewpager);

        pos = getIntent().getIntExtra(GALLERY_ITEM_POS, -1);
        callerId = getIntent().getIntExtra(CALLER_ID, -1);

        mAdaper = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return PhotoViewFragment.newInstance(i, callerId);
            }

            @Override
            public int getCount() {
                if(callerId == FavoriteFragment.FAVORITE_PHOTO_ID) {
                    return FavoriteFragment.mItems.size();
                }else {
                    return SearchFragment.mItems.size();
                }

            }
        };

        mViewPager = (ViewPager) findViewById(R.id.favorite_pager);
        mViewPager.setAdapter(mAdaper);
        mViewPager.setCurrentItem(pos);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int pos) {
                if(callerId == FavoriteFragment.FAVORITE_PHOTO_ID) {
                    if(pos == FavoriteFragment.mItems.size() - 1) {

                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
}
