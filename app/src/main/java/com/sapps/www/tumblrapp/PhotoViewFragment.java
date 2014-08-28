package com.sapps.www.tumblrapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

/**
 * Created by Hoang on 8/7/2014.
 */
public class PhotoViewFragment extends Fragment {
    public static String GALLERY_ITEM = "com.sapps.www.tumblrapp.gallery_item";
    public static String CALLER_ID = "com.sapps.www.tumblrapp.caller_id";

    private String blogName;
    private String photoUrl;
    private String postUrl;
    private int mPosition;
    private int mCallerId;
    private ArrayList<GalleryItem> mItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt(GALLERY_ITEM);
        mCallerId = getArguments().getInt(CALLER_ID);

        if(mCallerId == FavoriteFragment.FAVORITE_PHOTO_ID) {
            mItems = FavoriteGalleryItemLab.get(getActivity()).getFavoriteGalleryItems();
        }else {
            mItems = SearchGalleryItemLab.get(getActivity()).getSearchGalleryItems();
        }

        GalleryItem item = mItems.get(mPosition);
        blogName = item.getBlogName();
        photoUrl = item.getLargeUrl();
        postUrl = item.getPostUrl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.photo_view, container, false);

        final TouchImageView imageView = (TouchImageView) v.findViewById(R.id.photo_dialog_imageview);
        imageView.setMaxZoom(4f);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(postUrl);
                Intent intent = new Intent(getActivity(), PhotoPageActivity.class);
                intent.putExtra(PhotoPageActivity.EXTRA_BLOG_NAME, blogName);
                intent.setData(uri);
                startActivity(intent);
                getActivity().finish();
            }
        });


        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        if(photoUrl != null) {
            Ion.with(this)
                    .load(photoUrl)
                    .noCache()
                    .progressBar(progressBar)
                    .withBitmap()
                    .intoImageView(imageView)
                    .setCallback(new FutureCallback<ImageView>() {
                        @Override
                        public void onCompleted(Exception e, ImageView imageView) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }

        return v;
    }

    public static PhotoViewFragment newInstance(int pos, int id) {
        Bundle bundle = new Bundle();
        bundle.putInt(GALLERY_ITEM, pos);
        bundle.putInt(CALLER_ID, id);

        PhotoViewFragment fragment = new PhotoViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
