package ru.ifmo.md.lesson8;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import ru.ifmo.md.lesson8.DataClasses.City;
import ru.ifmo.md.lesson8.DataClasses.WeatherContentProvider;
import ru.ifmo.md.lesson8.DataClasses.WeatherManager;
import ru.ifmo.md.lesson8.WeatherLoaderClasses.WeatherLoader;

public class MainActivity extends ActionBarActivity
        implements CitiesListFragment.OnItemSelectedListener {
    DrawerLayout mDrawer;
    ActionBarDrawerToggle mToggle;

    City curCity;

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    public final int mDrawerOpen = Gravity.START;

    public interface CityChangedListener {
        void changeCity(City newCity);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (curCity == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putString("city", curCity.getCityName());
        bundle.putString("country", curCity.getCountryName());
        bundle.putInt("woeid", curCity.getWoeid());
        return bundle;
    }

    CityChangedListener myCallback;

    public void loadWeather() {
        if (curCity == null) {
            return;
        }
        Intent intent = new Intent(this, WeatherLoader.class);
        intent.putExtra("woeid", curCity.getWoeid());
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mToggle != null) {
            mToggle.syncState();
        }

//        WeatherManager.addCity(getContentResolver(), "Moscow", "Russia");
//        WeatherManager.addCity(getContentResolver(), "Almaty", "Kazakhstan");

        City spbCity = new City("St. Petersburg", "Russia", 2123260);
        WeatherManager.addCity(getContentResolver(), spbCity, WeatherContentProvider.isImportant);
//        WeatherManager.setImportant(getContentResolver(), "St. Petersburg", "Russia", WeatherContentProvider.isImportant);

        Fragment weatherFragment = getFragmentManager().findFragmentByTag("weather_frag");
        try {
            myCallback = (CityChangedListener) weatherFragment;
            if (myCallback == null) {
                Log.i("Creating callback", "Weather fragment wasn't created");
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(weatherFragment.toString()
                    + " must implement OnItemSelectedListener");
        }

        Bundle bundle = (Bundle) getLastCustomNonConfigurationInstance();
        if (bundle == null) {
            if (mDrawer != null) {
                mDrawer.openDrawer(mDrawerOpen);
            }
        } else {
            curCity = new City(bundle.getString("city"), bundle.getString("country"), bundle.getInt("woeid"));
            wasSelected(curCity);
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
                if (mDrawer.isDrawerOpen(mDrawerOpen)) {
                    mDrawer.closeDrawers();
                } else {
                    mDrawer.openDrawer(mDrawerOpen);
                }
                return true;
            }
        }
        if (item.getItemId() == R.id.action_refresh) {
            if (curCity == null) {
                if (mDrawer != null) {
                    mDrawer.openDrawer(mDrawerOpen);
                }
            } else {
                loadWeather();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer);
        mDrawer = (DrawerLayout)findViewById(R.id.main_drawer);

        if (mDrawer == null) {
            CitiesListFragment citiesFragment = new CitiesListFragment();
            WeatherFragment weatherFragment = new WeatherFragment();

            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            fTrans.replace(R.id.weather_container, weatherFragment, "weather_frag");
            fTrans.replace(R.id.cities_list_container, citiesFragment);
            fTrans.commit();
        } else {
            WeatherFragment weatherFragment = new WeatherFragment();

            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            fTrans.replace(R.id.weather_container, weatherFragment, "weather_frag");
            fTrans.commit();

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

    @Override
    public void wasSelected(City newCity) {
        if (newCity == null) {
            return;
        }
        hideKeyboard();

        curCity = newCity;
        if (WeatherManager.getCityId(getContentResolver(), newCity) == -1) {
            WeatherManager.addCity(getContentResolver(), newCity);
        }
        if (WeatherManager.getForecastByCity(getContentResolver(), newCity).getCount() == 0) {
            loadWeather();
        }
        if (mDrawer != null) {
            mDrawer.closeDrawers();
        }
        myCallback.changeCity(curCity);
    }
}