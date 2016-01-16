package com.arthur_guo.SafeToEat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.arthur_guo.SafeToEat.InfoAdapter.Restaurant;
import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantDBAdapter;
import com.arthur_guo.SafeToEat.InfoAdapter.RestaurantHelper;
import com.arthur_guo.SafeToEat.R;

import java.sql.SQLException;
import java.util.ArrayList;

import Tabs.SlidingTabLayout;


public class RestaurantLauncher extends ActionBarActivity {

    ViewPager mPager;
    SlidingTabLayout mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_launcher);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //Initialize restauranthelper
        RestaurantDBAdapter dbAdapter = new RestaurantDBAdapter(this);
        try {
            dbAdapter.createDatabase();
            dbAdapter.open();
            RestaurantHelper.initialize(dbAdapter.getRestaurants(), this);
            dbAdapter.close();
        } catch (SQLException e) {
            e.printStackTrace();
            RestaurantHelper.initialize(new ArrayList<Restaurant>(),this);
        }

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);
        mTabs.setViewPager(mPager);

        //Load the correct tab
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int currTab = 0;
        switch (prefs.getString("default_page", "null")) {
            case "Display All":
                currTab = 1;
                break;
            case "Nearby":
                currTab = 2;
                break;
            case "Bookmarked":
                currTab = 3;
                break;
            default:
                currTab = 1;
        }
        mPager.setCurrentItem(currTab);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant_launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_about){
            Intent intent = new Intent(getApplicationContext(), About.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        String[] tabs;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = new String[]{"Display All", "Nearby", "My Bookmarks"};
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new DisplayAll();
            } else if (position == 1) {
                return new NearMe();
            } else /*if (position == 2)*/ {
                return new FavouriteRestaurants();
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
