package com.thomastriplett.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Thomas on 4/19/2018.
 */

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    private boolean running = true;
    private ServiceReceiver serviceReceiver = new ServiceReceiver();
    private ArrayList<Article> articleList = new ArrayList<>();


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter filter1 = new IntentFilter(MainActivity.ACTION_MSG_TO_SERVICE);
        registerReceiver(serviceReceiver, filter1);

        //Creating new thread for my service
        //ALWAYS write your long running tasks in a separate thread, to avoid ANR

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (running) {

                    while(articleList.isEmpty()) {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // articleList is not empty
                    Intent intent = new Intent(MainActivity.ACTION_NEWS_STORY);
                    intent.putExtra("articles",articleList);
                    sendBroadcast(intent);
                    Log.d(TAG,"Sent articles to MainActivity");
                    articleList.clear();
                }
                Toast.makeText(NewsService.this, "News service has shut down", Toast.LENGTH_SHORT).show();

                Log.d(TAG, "run: Ending loop");
            }
        }).start();


        return Service.START_STICKY;
    }


    protected void setArticles(ArrayList<Article> newList) {
        articleList.clear();
        articleList.addAll(newList);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        unregisterReceiver(serviceReceiver);
        running = false;
        super.onDestroy();
    }

    class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_MSG_TO_SERVICE:
                    if (intent.hasExtra("source")) {
                        Source source = intent.getParcelableExtra("source");
                        String sourceName = source.getName();
                        Log.d(TAG,"The source is: "+sourceName);
                        // create and execute News Article Downloader Async Task
                        new NewsArticleDownloader(NewsService.this).execute(sourceName);
                    }
                    break;
            }
        }
    }
}
