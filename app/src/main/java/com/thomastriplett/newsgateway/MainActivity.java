package com.thomastriplett.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static final String ACTION_NEWS_STORY = "ANS";
    static final String ACTION_MSG_TO_SERVICE = "AMTS";
    private NewsReceiver newsReceiver = new NewsReceiver();

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    //private ArrayList<String> items = new ArrayList<>();
    private ArrayAdapter<?> arrayAdapter;

    private Menu actionMenu;

    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments = new ArrayList<>();
    private ViewPager pager;

    private HashMap<String, Source> sourceMap = new HashMap<>();
    private ArrayList<String> sourceList = new ArrayList<>();
    private ArrayList<String> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);

        newsReceiver = new NewsReceiver();

        IntentFilter filter1 = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter1);

        // setup stuff

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_list_item, sourceList);
        mDrawerList.setAdapter(arrayAdapter);

        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                    }
                }
        );

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        pager.setBackground(getResources().getDrawable(R.drawable.background, this.getTheme()));

        Log.d(TAG,"It's getting to the downloader");
        new NewsSourceDownloader(this).execute("");
    }

    private void selectItem(int position) {
        pager.setBackground(null);
        setTitle(sourceList.get(position));
        Intent intent = new Intent(ACTION_MSG_TO_SERVICE);
        String sourceName = sourceList.get(position);
        Log.d(TAG, "You clicked: "+sourceName);
        Source currentSource = sourceMap.get(sourceName);
        intent.putExtra("source", currentSource);
        sendBroadcast(intent);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);
        actionMenu = menu;
        for (int i=0; i < categoryList.size(); i++){
            menu.add(categoryList.get(i));
            Log.d(TAG,"The category is: "+categoryList.get(i));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        String category = item.toString();
        Log.d(TAG, "You picked: "+category);
        new NewsSourceDownloader(this).execute(category);

        return true;

    }

    protected void setSources(ArrayList<Source> sources, ArrayList<String> categories){
        // do stuff
        sourceMap.clear();
        sourceList.clear();

        for (int i=0; i<sources.size(); i++){
            sourceList.add(sources.get(i).getName());
            sourceMap.put(sources.get(i).getName(),sources.get(i));
            //Log.d(TAG,"The source is: "+sources.get(i).getName());
        }

        if(categoryList.size() == 0){
            categoryList.add("all");
            actionMenu.add("all");
            for (int i=0; i<categories.size(); i++){
                categoryList.add(categories.get(i));
                actionMenu.add(categories.get(i));
            }
            //
            // categoryList.addAll(categories);
        }


        // notify drawer adapter that dataset has changed
        arrayAdapter.notifyDataSetChanged();
    }

    class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"onReceive is activated");
            switch (intent.getAction()) {
                case ACTION_NEWS_STORY:
                    ArrayList<Article> newList = new ArrayList<>();
                    if (intent.hasExtra("articles"))
                        newList = intent.getParcelableArrayListExtra("articles");
                        reDoFragments(newList);
                    break;
            }
        }

        public void reDoFragments(ArrayList<Article> newList){
            // do stuff with fragments
            for (int i=0; i<pageAdapter.getCount(); i++){
                pageAdapter.notifyChangeInPosition(i);
            }

            fragments.clear();

            for (int i=0; i<newList.size(); i++){
                fragments.add(MyFragment.newInstance(newList.get(i),MainActivity.this,i));
            }

            pageAdapter.notifyDataSetChanged();
            pager.setCurrentItem(0);
        }
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(newsReceiver);
        super.onDestroy();
    }
}
