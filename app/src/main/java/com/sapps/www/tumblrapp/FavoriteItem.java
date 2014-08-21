package com.sapps.www.tumblrapp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Hoang on 8/14/2014.
 */
public class FavoriteItem {

    private static final String JSON_BLOG_NAME = "blogname";
    private static final String JSON_AVATAR = "avatar";

    private String mBlogName;
    private String mAvatarUrl;

    public FavoriteItem(JSONObject object) throws JSONException{
        mBlogName = object.getString(JSON_BLOG_NAME);
        mAvatarUrl = object.getString(JSON_AVATAR);
    }

    public FavoriteItem() {
        mAvatarUrl = null;
        mBlogName = null;
    }

    public String getBlogName() {
        return mBlogName;
    }

    public void setBlogName(String mBlogName) {

        this.mBlogName = mBlogName + ".tumblr.com";
    }

    public String getAvatar() {
        return mAvatarUrl;
    }

    public void setAvatar(String blogName) {
        this.mAvatarUrl = "http://api.tumblr.com/v2/blog/"+ blogName +"/avatar/48";
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject object = new JSONObject();
        object.put(JSON_BLOG_NAME, mBlogName);
        object.put(JSON_AVATAR, mAvatarUrl);

        return object;
    }

    public String toString() {
        return mBlogName;
    }
}
