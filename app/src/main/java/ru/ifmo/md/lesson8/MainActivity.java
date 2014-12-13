package ru.ifmo.md.lesson8;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import ru.ifmo.md.lesson8.DataClasses.City;
import ru.ifmo.md.lesson8.DataClasses.WeatherManager;

public class MainActivity extends ActionBarActivity
        implements CitiesListFragment.OnItemSelectedListener {
    private final String TAG = getClass().getName();

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private DialogFragment settingsDialog;
    private LocationManager locationManager;

    private Handler mHandlerGPS;
    private Handler mHandlerNetwork;

    final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (mHandlerGPS != null) {
                mHandlerGPS.removeCallbacksAndMessages(null);
            }
            if (mHandlerNetwork != null) {
                mHandlerNetwork.removeCallbacksAndMessages(null);
            }
            Log.d(TAG, "Current location is " + location.getLatitude() + ", " + location.getLongitude());

            try {
                City curCity = new DetectingCity(getBaseContext()).execute(location.getLatitude(), location.getLongitude()).get();
                wasSelected(curCity);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    Looper determineLocationGPS() {
        Looper looper = Looper.myLooper();
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, looper);
        return looper;
    }

    Looper determineLocationNetwork() {
        Looper looper = Looper.myLooper();
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, looper);
        return looper;
    }


    void determineLocation() {
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        long gpsInterval = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ? 10000 : 0;
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(
                    getBaseContext(),
                    R.string.error_location_all,
                    Toast.LENGTH_LONG)
                    .show();
            Log.d(TAG, "geodata services are off");
            return;
        }
        mHandlerGPS = new Handler(determineLocationGPS());
        mHandlerGPS.postDelayed(new Runnable() {
            @Override
            public void run() {
                locationManager.removeUpdates(locationListener);
                Toast.makeText(getBaseContext(), R.string.error_location_gps, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Unable to determine location via GPS");
                mHandlerNetwork = new Handler(determineLocationNetwork());
                mHandlerGPS.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        locationManager.removeUpdates(locationListener);
                        Toast.makeText(getBaseContext(), R.string.error_location_network, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Unable to determine location via network");

                    }
                }, 10000); // trying to detect coordinates for 2 seconds
            }
        }, gpsInterval); // trying to detect coordinates for 10 seconds
    }


    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public final int mDrawerOpen = Gravity.START;

    public interface CityChangedListener {
        void changeCity();
    }

    CityChangedListener myCallback;

    void setMenuItemsVisibility(boolean visibility) {
        menu.findItem(R.id.action_get_location).setVisible(visibility);
        menu.findItem(R.id.action_refresh).setVisible(visibility);
        menu.findItem(R.id.action_update_settings).setVisible(visibility);
    }

    private Menu menu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        this.menu = menu;
        if (mDrawer != null && mDrawer.isDrawerOpen(mDrawerOpen)) {
            setMenuItemsVisibility(false);
        }
        return super.onCreateOptionsMenu(menu);
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
            boolean res = WeatherManager.refresh(this);
            if (res == false) {
                if (mDrawer != null) {
                    mDrawer.openDrawer(mDrawerOpen);
                }
            }
            return true;
        } else if (item.getItemId() == R.id.action_update_settings) {
            settingsDialog.show(getSupportFragmentManager(), "automatic_update");
            return true;
        } else if (item.getItemId() == R.id.action_get_location) {
            determineLocation();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                    setMenuItemsVisibility(true);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    getSupportActionBar().setTitle("Cities");
                    setMenuItemsVisibility(false);
                }
            };
            mDrawer.setDrawerListener(mToggle);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        settingsDialog = new SettingsDialog();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mToggle != null) {
            mToggle.syncState();
        }

//        City spbCity = new City("St. Petersburg", "Russia", 2123260);
//        WeatherManager.addCity(getContentResolver(), spbCity, WeatherContentProvider.isImportant);

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

        SharedPreferences prefs = getPreferences(0);
        if (prefs.contains(City.CITY_BUNDLE_KEY) && prefs.contains(City.COUNTRY_BUNDLE_KEY) && prefs.contains(City.WOEID_BUNDLE_KEY)) {
            City curCity = new City(
                    prefs.getString(City.CITY_BUNDLE_KEY, null),
                    prefs.getString(City.COUNTRY_BUNDLE_KEY, null),
                    prefs.getInt(City.WOEID_BUNDLE_KEY, 0)
            );
            wasSelected(curCity);
        }
        hideKeyboard();
        Intent intent = new Intent(this, UpdateService.class);
        startService(intent);
    }

    @Override
    public void wasSelected(City newCity) {
        if (newCity == null) {
            return;
        }
        hideKeyboard();
        WeatherManager.setCurCity(newCity);
        SharedPreferences prefs = getPreferences(0);
        prefs.edit()
                .putString(City.CITY_BUNDLE_KEY, newCity.getCityName())
                .putString(City.COUNTRY_BUNDLE_KEY, newCity.getCountryName())
                .putInt(City.WOEID_BUNDLE_KEY, newCity.getWoeid())
                .commit();
        if (WeatherManager.getCityId(getContentResolver(), newCity) == -1) {
            WeatherManager.addCity(getContentResolver(), newCity);
        }
        if (WeatherManager.getForecastByCity(getContentResolver(), newCity).getCount() == 0) {
            WeatherManager.loadWeather(this, WeatherManager.getCurCity());
        }
        if (mDrawer != null) {
            mDrawer.closeDrawers();
        }
        myCallback.changeCity();
    }
}