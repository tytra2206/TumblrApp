package com.sapps.www.tumblrapp;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Hoang on 8/9/2014.
 */
public class PhotoPageActivity extends Activity {

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private String mBlogUrl;
    private String mBlogName;
    public static final String EXTRA_BLOG_NAME = "com.sapps.www.tumblrapp.blog_name";
    public static final String TAG = "PhotoPageActivity";
    private ArrayList<FavoriteItem> mFavoriteItems;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_web_page);

        mFavoriteItems = FavoriteItemLab.get(this).getFavoriteItems();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if(NavUtils.getParentActivityName(this) != null) {
                getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        mBlogUrl = getIntent().getData().toString();
        mBlogName = getIntent().getStringExtra(EXTRA_BLOG_NAME);

        final ActionBar actionBar = getActionBar();
        actionBar.setTitle(mBlogName);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(100); // WebChromeClient reports in range 0-100

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });


        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView webView, int progress) {
                if (progress == 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(progress);
                }
            }
        });

        mWebView.loadUrl(mBlogUrl);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            case R.id.add_favorite_web_page:
                for(int i = 0; i < mFavoriteItems.size(); i++) {
                    if(mFavoriteItems.get(i).getBlogName().equals(mBlogName + ".tumblr.com")){
                        Toast.makeText(this, "Blog already in favorites", Toast.LENGTH_SHORT)
                                .show();
                        return false;
                    }
                }

                FavoriteItem favoriteItem = new FavoriteItem();
                favoriteItem.setBlogName(mBlogName);
                favoriteItem.setAvatar(favoriteItem.getBlogName());
                mFavoriteItems.add(favoriteItem);

                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_web_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        FavoriteItemLab.get(this).saveFavoriteItems();
        super.onPause();
    }
}
