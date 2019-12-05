package com.thomastriplett.newsgateway;

import android.app.Service;
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

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

/**
 * Created by Thomas on 4/19/2018.
 */
// API key = f239676291cf47b7887da5d2b9a0c1a1

public class NewsArticleDownloader extends AsyncTask<String, Void, String> {

    private NewsService newsService;

    public NewsArticleDownloader(NewsService ns) {
        newsService = ns;
    }

    @Override
    protected String doInBackground(String... strings) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url;
            if(strings[0].contains(" ") || strings[0].contains("/")){
                String newString;
                if(strings[0].contains("/")) {
                    newString = "reddit-r-all";
                }
                else {
                    newString = strings[0].replace(" ", "-");
                }
                url = new URL("https://newsapi.org/v1/articles?source=" + newString + "&apiKey=f239676291cf47b7887da5d2b9a0c1a1");
            }
            else {
                url = new URL("https://newsapi.org/v1/articles?source=" + strings[0] + "&apiKey=f239676291cf47b7887da5d2b9a0c1a1");
            }
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
            ArrayList<Article> articleList = new ArrayList<>();

            JSONObject jsonObj = new JSONObject(s);
            JSONArray articles = jsonObj.getJSONArray("articles");

            for(int i=0; i<articles.length(); i++){
                JSONObject c = articles.getJSONObject(i);
                String author = c.getString("author");
                String title = c.getString("title");
                String description = c.getString("description");
                String imageURL = c.getString("urlToImage");
                String date = c.getString("publishedAt");
                String url = c.getString("url");
                Article currentArticle = new Article(author, title, description, imageURL, date, url);
                articleList.add(currentArticle);
            }
            newsService.setArticles(articleList);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }
}
