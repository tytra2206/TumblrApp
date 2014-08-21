package com.sapps.www.tumblrapp;

/**
 * Created by Hoang on 8/7/2014.
 */
public class GalleryItem {
    private String mSmallUrl;
    private String mLargeUrl;
    private String mPostUrl;
    private String mBlogName;

    public String getSmallUrl() {
        return mSmallUrl;
    }

    public void setSmallUrl(String mSmallUrl) {
        this.mSmallUrl = mSmallUrl;
    }

    public String getLargeUrl() {
        return mLargeUrl;
    }

    public void setLargeUrl(String mLargeUrl) {
        this.mLargeUrl = mLargeUrl;
    }

    public String getPostUrl() {
        return mPostUrl;
    }

    public void setPostUrl(String mPostUrl) {
        this.mPostUrl = mPostUrl;
    }

    public String getBlogName() {
        return mBlogName;
    }

    public void setBlogName(String mBlogName) {
        this.mBlogName = mBlogName;
    }
}
