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
    private int mOffset;
    private static final String TAG = "FavoriteFragment";
    private String mBlogUrl;
    private ProgressBar mProgressBar;
    private ArrayList<FavoriteItem> mFavoriteItems;
    private FavoriteImageAdapter mFavoriteAdapter;
    private TextView mLoadingTexView;
    private BackgroundFetchr mBackgroundFetchr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mBlogUrl = "";
        mOffset = 0;
        mFavoriteItems = FavoriteItemLab.get(getActivity()).getFavoriteItems();
        mFavoriteAdapter = new FavoriteImageAdapter();
        mBackgroundFetchr = new BackgroundFetchr();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.favorite_fragment, container, false);

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
                    mItems = null;
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
                        && mBackgroundFetchr.getStatus() != AsyncTask.Status.RUNNING) {
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
                    Intent intent = new Intent(getActivity(), PhotoViewActivity.class);
                    GalleryItem item = mItems.get(pos);
                    intent.putExtra(PhotoViewActivity.BLOG_URL, item.getPostUrl());
                    intent.putExtra(PhotoViewActivity.PHOTO_URL, item.getLargeUrl());
                    intent.putExtra(PhotoViewActivity.BLOG_NAME, item.getBlogName());
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
            mGallery.setVisibility(View.INVISIBLE);
            mLoadingTexView.setVisibility(View.VISIBLE);
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
                if(mItems == null) {
                    mItems = galleryItems;
                }else {
                    mItems.addAll(galleryItems);
                }
                updateAdapter();
                mProgressBar.setVisibility(View.GONE);
            }
            mGallery.setVisibility(View.VISIBLE);
            mLoadingTexView.setVisibility(View.INVISIBLE);
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
        FavoriteItemLab.get(getActivity()).saveFavoriteItems();
        Log.i(TAG, "onPause() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        mFavoriteAdapter.notifyDataSetChanged();
        Log.i(TAG, "onResume() called");
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
