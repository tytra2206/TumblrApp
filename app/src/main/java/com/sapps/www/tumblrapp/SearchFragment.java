package com.sapps.www.tumblrapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Hoang on 8/12/2014.
 */
public class SearchFragment extends Fragment {

    private static final String TAG ="SearchFragment";
    private static final String TIMESTAMP = "com.sapps.www.tumblrapp.timestamp";

    private ArrayList<GalleryItem> mItems;
    public GridView mGridView;
    private GridViewAdapter mAdapter;
    private ProgressBar mProgressBar;
    public static int mBefore;
    private String mQuery;
    private BackgroundFetchr mBackgroundFetchr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit().putString(TIMESTAMP, "")
                .commit();
        mBefore = 0;
        mBackgroundFetchr = new BackgroundFetchr();
        Log.i(TAG, "onCreate() called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_fragment, container, false);

        mGridView = (GridView) v.findViewById(R.id.search_gridView);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if(mItems != null && mItems.get(pos) != null) {
                    Intent intent = new Intent(getActivity(), PhotoViewActivity.class);
                    GalleryItem item = mItems.get(pos);
                    intent.putExtra(PhotoViewActivity.BLOG_URL, item.getPostUrl());
                    intent.putExtra(PhotoViewActivity.PHOTO_URL, item.getLargeUrl());
                    intent.putExtra(PhotoViewActivity.BLOG_NAME, item.getBlogName());
                    startActivity(intent);
                }
            }
        });

        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                if(i + i2 >= i3 && mBefore != 0 && isNetworkAvailable()
                        && mBackgroundFetchr.getStatus() != AsyncTask.Status.RUNNING) {
                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    String lastTimeStamp = prefs.getString(TIMESTAMP, null);

                    if(lastTimeStamp == null ||lastTimeStamp.equals(mBefore+"")) {
                        return;
                    }else {
                        mBackgroundFetchr.execute(mQuery);
                        Log.i(TAG, "onScroll called");
                        Log.i(TAG, "mBefore: " + mBefore);
                        PreferenceManager.getDefaultSharedPreferences(getActivity())
                                .edit().putString(TIMESTAMP, mBefore + "")
                                .commit();
                    }
                }
            }
        });

        final SearchView searchView = (SearchView) v.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.i(TAG, "onQueryTextSubmit called");

                if(isNetworkAvailable()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mItems = null;
                    mAdapter = null;
                    mBefore = 0;
                    mQuery = query.replace(" ", "%20");
                    new BackgroundFetchr().execute(mQuery);
                    searchView.clearFocus();
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Network not available", Toast.LENGTH_SHORT).show();
                    searchView.clearFocus();
                    return false;
                }

            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        mProgressBar = (ProgressBar) v.findViewById(R.id.search_progressbar);
        mProgressBar.setVisibility(View.INVISIBLE);
        Log.i(TAG, "onCreateView() called");
        return v;
    }

    private class BackgroundFetchr extends AsyncTask<String, Void, ArrayList<GalleryItem>> {

        @Override
        protected ArrayList<GalleryItem> doInBackground(String... strings) {
            String url = "http://api.tumblr.com/v2/tagged?tag=" + strings[0] +
                        "&api_key=9BrmFSYl1n5zj72Sb7hW5Rj0GASGNgxqwsdYpglt2fkkiEigxT&before=" + mBefore;

            return TumblrFetchr.parseTaggedItems(TumblrFetchr.fetchUrl(url));

        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            if(galleryItems != null && !galleryItems.isEmpty()) {
                if(mItems == null) {
                    mItems = galleryItems;
                }else {
                    mItems.addAll(galleryItems);
                }
                updateAdapter();
            }

            if(galleryItems == null || galleryItems.isEmpty()) {
                if(mItems == null) {
                    Toast.makeText(getActivity(), "Search Result: 0", Toast.LENGTH_SHORT).show();
                    updateAdapter();
                }
            }
            mProgressBar.setVisibility(View.GONE);
            mBackgroundFetchr = new BackgroundFetchr();
        }
    }

    private void updateAdapter() {
        if (getActivity() == null || mGridView == null) return;

        if (mItems != null) {
            if(mAdapter == null) {
                mAdapter = new GridViewAdapter(mItems, getActivity());
                mGridView.setAdapter(mAdapter);
            }else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mGridView.setAdapter(null);
        }
    }

    public boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprication")
        boolean isAvailable = manager.getBackgroundDataSetting() &&
                manager.getActiveNetworkInfo() != null;
        return isAvailable;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume() called");
    }
}
