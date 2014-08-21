package com.sapps.www.tumblrapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Hoang on 8/12/2014.
 */
public class TumblrFetchr {

    private static final String TAG = "TumblrFetchr";

    public static String fetchUrl(String urlString) {

        Log.i(TAG, "Url: " + urlString);
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();
            connection.disconnect();
            Log.i(TAG, "Received json: " + builder.toString());
            return builder.toString();

        } catch (MalformedURLException e) {
            Log.e(TAG, "Bad URL", e);
        }catch (IOException e) {
            Log.e(TAG, "Bad connection", e);
        }

        return null;
    }

    public static ArrayList<GalleryItem> parseBlogItems(String jsonString) {

        ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
        String postUrl;
        String blogName;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject response = jsonObject.getJSONObject("response");
            blogName = response.getJSONObject("blog").getString("name");

            JSONArray posts = response.getJSONArray("posts");
            for(int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                postUrl = post.getString("short_url").replace("\\", "");
                JSONArray photos = post.getJSONArray("photos");
                for(int j = 0; j < photos.length(); j++) {
                    JSONObject photo = photos.getJSONObject(j);
                    JSONArray alt_sizes = photo.getJSONArray("alt_sizes");
                    GalleryItem item = new GalleryItem();
                    for(int k = 0; k < alt_sizes.length(); k++) {
                        JSONObject url = alt_sizes.getJSONObject(k);
                        String urlString = url.getString("url");
                        if(k == 2) {
                            item.setLargeUrl(urlString);
                        } else if(k == 4) {
                            item.setSmallUrl(urlString);
                        }
                    }

                    if(item.getSmallUrl() != null && item.getLargeUrl() != null) {
                        item.setPostUrl(postUrl);
                        item.setBlogName(blogName);
                        items.add(item);
                    }
                }
            }
            return items;
        }catch (JSONException e) {
            Log.e(TAG, "Error parsing items", e);
        }

        return null;
    }

    public static ArrayList<GalleryItem> parseTaggedItems(String jsonString) {
        ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
        String postUrl;
        String blogName;
        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray response = jsonObject.getJSONArray("response");
            for(int i = 0; i<response.length(); i++) {
                JSONObject post = response.getJSONObject(i);
                blogName = post.getString("blog_name");
                SearchFragment.mBefore = post.getInt("timestamp");

                if(!post.getString("type").equals("photo")) {
                    continue;
                }

                JSONArray photos = post.getJSONArray("photos");
                postUrl = post.getString("short_url");
                postUrl.replace("\\", "");

                for(int j = 0; j < photos.length(); j++) {
                    JSONObject photo = photos.getJSONObject(j);
                    JSONArray alt_sizes = photo.getJSONArray("alt_sizes");
                    GalleryItem item = new GalleryItem();
                    item.setPostUrl(postUrl);
                    item.setBlogName(blogName);
                    for(int k = 0; k < alt_sizes.length(); k++) {
                        JSONObject url = alt_sizes.getJSONObject(k);
                        String urlString = url.getString("url");

                        if(k == 2) {
                            item.setLargeUrl(urlString);
                        } else if(k == 4) {
                            item.setSmallUrl(urlString);
                        }
                    }
                    if(item.getSmallUrl() != null && item.getLargeUrl() != null) {
                        items.add(item);
                    }
                }
            }

            return items;
        }catch (JSONException e) {
            Log.e(TAG, "Error parsing items", e);
        }
        return null;
    }
}
