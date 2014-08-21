package com.sapps.www.tumblrapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
/**
 * Created by Hoang on 8/7/2014.
 */
public class PhotoViewActivity extends Activity {

    private static final String TAG="PhotoDialog";
    public static final String PHOTO_URL= "com.sapps.www.tumblrapp.photo_url";
    public static final String BLOG_URL= "com.sapps.www.tumblrapp.blog_url";
    public static final String BLOG_NAME= "com.sapps.www.tumblrapp.blog_name_photoview";

    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photo_view);

        final String photoUrl = getIntent().getStringExtra(PHOTO_URL);
        final String blogUrl = getIntent().getStringExtra(BLOG_URL);
        final String blogName = getIntent().getStringExtra(BLOG_NAME);

        final TouchImageView imageView = (TouchImageView) findViewById(R.id.photo_dialog_imageview);
        imageView.setMaxZoom(4f);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(blogUrl);
                Intent intent = new Intent(mContext, PhotoPageActivity.class);
                intent.putExtra(PhotoPageActivity.EXTRA_BLOG_NAME, blogName);
                intent.setData(uri);
                startActivity(intent);
                finish();
            }
        });
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

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
    }
}
