package com.sapps.www.tumblrapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Hoang on 8/14/2014.
 */
public class JSONSerializer {
    private static final String TAG = "JSONSerializer";
    private Context mContext;
    private String mFileName;

    public JSONSerializer(Context context, String fileName) {
        mContext = context;
        mFileName = fileName;
    }

    public void saveFavoriteItems(ArrayList<FavoriteItem> items) throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for(FavoriteItem item : items) {
            array.put(item.toJSON());
        }

        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        }finally {
            if(writer != null) {
                writer.close();
            }
        }
    }

    public ArrayList<FavoriteItem> loadFavoriteItems() throws JSONException, IOException{
        ArrayList<FavoriteItem> items = new ArrayList<FavoriteItem>();
        BufferedReader reader = null;
        try {
            InputStream in = mContext.openFileInput(mFileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            // Parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(builder.toString())
                    .nextValue();

            for (int i = 0; i < array.length(); i++) {
                items.add(new FavoriteItem(array.getJSONObject(i)));
            }
        }catch (FileNotFoundException e) {
            // Ignore this one; it happens when starting fresh
        } finally {
            if (reader != null)
                reader.close();
        }
        return items;
    }
}
