package com.thomastriplett.newsgateway;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

/**
 * Created by Thomas on 4/19/2018.
 */
// API key = f239676291cf47b7887da5d2b9a0c1a1

public class NewsSourceDownloader extends AsyncTask<String, Void, String> {

    private MainActivity mainActivity;
    private String category;

    public NewsSourceDownloader(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url;
            if(strings[0].equals("") || strings[0].equals("all")) {
                url = new URL("https://newsapi.org/v1/sources?language=en&country=us&apiKey=f239676291cf47b7887da5d2b9a0c1a1");
            }
            else{
                url = new URL("https://newsapi.org/v1/sources?language=en&country=us&category="+strings[0]+"&apiKeyf239676291cf47b7887da5d2b9a0c1a1");
            }
            //Log.d(TAG, "The URL is: "+url);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            ArrayList<Source> sourceList = new ArrayList<>();
            ArrayList<String> categoryList = new ArrayList<>();

            JSONObject jsonObj = new JSONObject(s);
            JSONArray sources = jsonObj.getJSONArray("sources");

            for(int i=0; i<sources.length(); i++){
                JSONObject c = sources.getJSONObject(i);
                String id = c.getString("id");
                String name = c.getString("name");
                String url = c.getString("url");
                String category = c.getString("category");
                //Log.d(TAG, "The source is: "+name);
                Source currentSource = new Source(id, name, url, category);
                sourceList.add(currentSource);

                if(!categoryList.contains(category)){
                    categoryList.add(category);
                }
            }
            mainActivity.setSources(sourceList,categoryList);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }
}
