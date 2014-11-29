package ru.ifmo.md.lesson8;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;

import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import ru.ifmo.md.lesson8.DataClasses.CitiesListFragment;

public class MainActivity extends ActionBarActivity {
    DrawerLayout mDrawer;
    ActionBarDrawerToggle mToggle;
    boolean isBig = false;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mToggle != null) {
            mToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mToggle != null) {
            mToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle != null) {
            if (mToggle.onOptionsItemSelected(item)) {
                if (mDrawer.isDrawerOpen(Gravity.START)) {
                    mDrawer.closeDrawers();
                } else {
                    mDrawer.openDrawer(Gravity.START);
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer);

        mDrawer = (DrawerLayout)findViewById(R.id.main_drawer);

        if (mDrawer == null) {
            isBig = true;
        } else {
            isBig = false;
        }

        if (isBig) {
            WeatherFragment weatherFragment = new WeatherFragment();
            CitiesListFragment citiesFragment = new CitiesListFragment();
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            fTrans.add(R.id.weather_container, weatherFragment);
            fTrans.add(R.id.cities_list_container, citiesFragment);
            fTrans.commit();
        } else {
            mToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.drawer_open,
                    R.string.drawer_close) {
                @Override
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    getSupportActionBar().setTitle(R.string.app_name);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    getSupportActionBar().setTitle("Cities");
                }
            };
            mDrawer.setDrawerListener(mToggle);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }
}