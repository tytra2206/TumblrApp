package com.sapps.www.tumblrapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

/**
 * Created by Hoang on 8/12/2014.
 */
public class FavoriteFragment extends Fragment {

    private Gallery mGallery;
    private GridView mGridView;
    private GridViewAdapter mAdapter;
    private ArrayList<GalleryItem> mItems;
    public static int mOffset;
    private static final String TAG = "FavoriteFragment";
    private String mBlogUrl;
    private ProgressBar mProgressBar;
    private ArrayList<FavoriteItem> mFavoriteItems;
    private FavoriteImageAdapter mFavoriteAdapter;
    private TextView mLoadingTexView;
    private BackgroundFetchr mBackgroundFetchr;
    private boolean shouldKeepFetching;

    public static final int FAVORITE_PHOTO_ID = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mOffset = 0;
        mFavoriteItems = FavoriteItemLab.get(getActivity()).getFavoriteItems();
        mItems = FavoriteGalleryItemLab.get(getActivity()).getFavoriteGalleryItems();
        mFavoriteAdapter = new FavoriteImageAdapter();
        mBackgroundFetchr = new BackgroundFetchr();
        mBlogUrl = mFavoriteItems.get(0).getBlogName();
        shouldKeepFetching = true;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.favorite_fragment, container, false);

        if(isNetworkAvailable()) {
            mBackgroundFetchr.execute(mBlogUrl, mOffset + "");
        }

        mGallery = (Gallery) v.findViewById(R.id.favorite_gallery);
        registerForContextMenu(mGallery);

        mGallery.setAdapter(mFavoriteAdapter);
        mGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if(!isNetworkAvailable()) {
                    Toast.makeText(getActivity(), "Network not available", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!mBlogUrl.equals(mFavoriteItems.get(pos).getBlogName())) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mOffset = 0;
                    mBlogUrl = mFavoriteItems.get(pos).getBlogName();
                    FavoriteGalleryItemLab.get(getActivity()).clearFavoriteGalleryItems();
                    mAdapter = null;
                    mBackgroundFetchr.execute(mBlogUrl, mOffset + "");
                }
            }
        });

        mGridView = (GridView) v.findViewById(R.id.favorite_gridView);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                if(i + i2 >= i3 && !mBlogUrl.isEmpty() && isNetworkAvailable()
                        && mBackgroundFetchr.getStatus() != AsyncTask.Status.RUNNING
                        && shouldKeepFetching) {
                    Log.i(TAG, "onScroll called");
                    mOffset += 20;
                    mBackgroundFetchr.execute(mBlogUrl, mOffset + "");
                }
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if (mItems != null && mItems.get(pos) != null) {
                    if(mBackgroundFetchr.getStatus() == AsyncTask.Status.RUNNING) {
                        mBackgroundFetchr.cancel(true);
                        mBackgroundFetchr = new BackgroundFetchr();
                    }
                    Intent intent = new Intent(getActivity(), PhotoViewPagerActivity.class);
                    intent.putExtra(PhotoViewPagerActivity.GALLERY_ITEM_POS, pos);
                    intent.putExtra(PhotoViewPagerActivity.CALLER_ID, FAVORITE_PHOTO_ID);
                    startActivity(intent);
                }
            }
        });

        mProgressBar = (ProgressBar) v.findViewById(R.id.favorite_progressbar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mLoadingTexView = (TextView)v.findViewById(R.id.loading_textview);
        mLoadingTexView.setVisibility(View.INVISIBLE);

        return v;
    }

    private class FavoriteImageAdapter extends ArrayAdapter<FavoriteItem> {

        public FavoriteImageAdapter() {
            super(getActivity(), 0, mFavoriteItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.favorite_image, parent, false);
            }

            FavoriteItem item = mFavoriteItems.get(position);

            final ImageView imageView = (ImageView)
                    convertView.findViewById(R.id.favorite_imageView);
            Ion.with(imageView).load(item.getAvatar());

            final TextView textView = (TextView)
                    convertView.findViewById(R.id.favorite_image_textview);

            textView.setText(item.getBlogName().substring(0, item.getBlogName().length()-11));

            return convertView;
        }
    }

    private class BackgroundFetchr extends AsyncTask<String, Void, ArrayList<GalleryItem>> {

        @Override
        protected void onPreExecute() {
            if(mGallery != null && mLoadingTexView != null) {
                mGallery.setVisibility(View.INVISIBLE);
                mLoadingTexView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected ArrayList<GalleryItem> doInBackground(String... strings) {

            String url =  "http://api.tumblr.com/v2/blog/" + strings[0] +
                        "/posts/photo?api_key=9BrmFSYl1n5zj72Sb7hW5Rj0GASGNgxqwsdYpglt2fkkiEigxT&offset="
                        + strings[1];

            return TumblrFetchr.parseBlogItems(TumblrFetchr.fetchUrl(url));

        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {

            if(galleryItems != null && !galleryItems.isEmpty()) {
               FavoriteGalleryItemLab.get(getActivity()).addFavoriteGalleryItems(galleryItems);
               updateAdapter();
               shouldKeepFetching = true;
            } else if(galleryItems == null || galleryItems.isEmpty()) {
                if(mItems.isEmpty()) {
                    Toast.makeText(getActivity(), "This blog contains no photos", Toast.LENGTH_SHORT).show();
                    shouldKeepFetching = false;
                    updateAdapter();
                }
            } else if (galleryItems.size() < 10) {
                mGallery.setVisibility(View.VISIBLE);
                mLoadingTexView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mBackgroundFetchr = new BackgroundFetchr();
                mOffset += 20;
                mBackgroundFetchr.execute(mBlogUrl, mOffset + "");
                updateAdapter();
            }
            mGallery.setVisibility(View.VISIBLE);
            mLoadingTexView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mBackgroundFetchr = new BackgroundFetchr();
        }
    }

    private void updateAdapter() {
        if (getActivity() == null || mGridView == null) return;

        if (!mItems.isEmpty()) {
            if(mAdapter == null ) {
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
        FavoriteItemLab.get(getActivity()).saveFavoriteItems();
        Log.i(TAG, "onPause() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter();
        Log.i(TAG, "onResume() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FavoriteGalleryItemLab.get(getActivity()).clearFavoriteGalleryItems();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible && mFavoriteAdapter != null) {
            mFavoriteAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.favorite_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        int pos = info.position;
        FavoriteItem favoriteItem = mFavoriteAdapter.getItem(pos);

        switch (item.getItemId()) {
            case R.id.favorite_delete_context:
                FavoriteItemLab.get(getActivity()).deleteFavoriteItem(favoriteItem);
                mFavoriteAdapter.notifyDataSetChanged();
                FavoriteItemLab.get(getActivity()).saveFavoriteItems();
                return true;
        }
        return super.onContextItemSelected(item);
    }


}
