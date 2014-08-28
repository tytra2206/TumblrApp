package com.sapps.www.tumblrapp;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener{

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                switch (i) {
                    case 0:
                        return new FavoriteFragment();

                    case 1:
                        return new SearchFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int pos) {
                if(pos == 1) {
                    FavoriteItemLab.get(getApplicationContext()).saveFavoriteItems();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        final ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(false);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.addTab(actionBar.newTab().setText(R.string.favourite_tab).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.search_tab).setTabListener(this));

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int pos) {
                actionBar.setSelectedNavigationItem(pos);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
