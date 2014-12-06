package ru.ifmo.md.lesson8;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ru.ifmo.md.lesson8.DataClasses.City;
import ru.ifmo.md.lesson8.DataClasses.ForecastAdapter;
import ru.ifmo.md.lesson8.DataClasses.WeatherContentProvider;
import ru.ifmo.md.lesson8.DataClasses.WeatherManager;

/**
 * Created by german on 28.11.14.
 */
public class WeatherFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        MainActivity.CityChangedListener {

    private City curCity;

    private int cityId;

    private ListView forecastList;
    private ForecastAdapter adapter;
    private LoaderManager loaderManager;

    private final int forecastLoaderID = 0;
    private final int curWeatherLoaderID = 1;

    // cw = current weather
    private TextView cwDateView;
    private TextView cwTimeView;
    private TextView cwCityView;
    private TextView cwCountryView;
    private TextView cwTempView;
    private ImageView cwCloudyView;
    private TextView cwWindView;
    private TextView cwHumidityView;
    private TextView cwPressureView;

    // "C" or "F"
    private String tempType = "C";

    private void initLoaders() {
        loaderManager = getLoaderManager();
        loaderManager.initLoader(forecastLoaderID, null, this);
        loaderManager.initLoader(curWeatherLoaderID, null, this);
    }

    private void restartLoaders() {
        loaderManager.restartLoader(forecastLoaderID, null, this);
        loaderManager.restartLoader(curWeatherLoaderID, null, this);
    }

    private void updateCurWeather(Cursor cursor) {
        if (cursor.getCount() != 0 && curCity != null) {
            System.out.println("ok " + cursor.getCount());
            cursor.moveToFirst();

            int curTemp = cursor.getInt(cursor.getColumnIndexOrThrow(WeatherContentProvider.CUR_WEATHER_TEMP));
            String curDate = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContentProvider.CUR_WEATHER_DATE));
            String curTime = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContentProvider.CUR_WEATHER_TIME));
            int curCode = cursor.getInt(cursor.getColumnIndexOrThrow(WeatherContentProvider.CUR_WEATHER_CODE));
            String curWind = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContentProvider.CUR_WEATHER_WIND));
            String curHumidity = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContentProvider.CUR_WEATHER_HUMIDITY));
            String curPressure = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContentProvider.CUR_WEATHER_PRESSURE));

            cwDateView.setText(curDate);
            cwTimeView.setText(curTime);
            cwCityView.setText(curCity.getCityName());
            cwCountryView.setText(curCity.getCountryName());
            cwTempView.setText(String.valueOf(curTemp) + "°" + tempType);
            cwCloudyView.setImageResource(WeatherManager.getCloudyId(curCode));
            cwWindView.setText("Wind: " + curWind);
            cwHumidityView.setText("Humidity: " + curHumidity);
            cwPressureView.setText("Pressure: " + curPressure);
        } else {
            System.out.println("not ok");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new ForecastAdapter(getActivity().getApplicationContext(), null, false);
        forecastList = (ListView) getView().findViewById(R.id.forecast_list);
        forecastList.setAdapter(adapter);

        cwDateView = (TextView) getView().findViewById(R.id.cur_weather_date);
        cwTimeView = (TextView) getView().findViewById(R.id.cur_weather_time);
        cwCityView = (TextView) getView().findViewById(R.id.cur_weather_city);
        cwCountryView = (TextView) getView().findViewById(R.id.cur_weather_country);
        cwTempView = (TextView) getView().findViewById(R.id.cur_weather_temp);
        cwCloudyView = (ImageView) getView().findViewById(R.id.cur_weather_cloudy);
        cwWindView = (TextView) getView().findViewById(R.id.cur_weather_wind);
        cwHumidityView = (TextView) getView().findViewById(R.id.cur_weather_humidity);
        cwPressureView = (TextView) getView().findViewById(R.id.cur_weather_pressure);

        initLoaders();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri content;
        String where;
        String[] whereArgs;
        if (id == forecastLoaderID) {
            content = WeatherContentProvider.FORECAST_CONTENT;
            where = WeatherContentProvider.FORECAST_CITY_ID + " = ? ";
            whereArgs = new String[] {
                    String.valueOf(cityId)
            };
        } else /*if (id == curWeatherLoaderID)*/ {
            content = WeatherContentProvider.CUR_WEATHER_CONTENT;
            where = WeatherContentProvider.CUR_WEATHER_CITY_ID + " = ? ";
            whereArgs = new String[] {
                    String.valueOf(cityId)
            };
        }

        CursorLoader res = new CursorLoader(
                getActivity().getApplicationContext(),
                content,
                null, where, whereArgs, null);
        System.out.println("loader created");
        return res;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == forecastLoaderID) {
            adapter.swapCursor(data);
        } else /*if (getId() == curWeatherLoaderID) */{
            updateCurWeather(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == forecastLoaderID) {
            adapter.swapCursor(null);
        }
        System.out.println("reset");
    }

    @Override
    public void changeCity(City newCity) {
        curCity = newCity;
        cityId = WeatherManager.getCityId(getActivity().getContentResolver(), curCity);

        Log.i("City changed", curCity.toString() + " (id" + cityId + ")");

        cwCityView.setText(curCity.getCityName());
        cwCountryView.setText(curCity.getCountryName());
        cwTempView.setText("");
        cwDateView.setText("");
        cwTimeView.setText("");
        cwWindView.setText("");
        cwHumidityView.setText("");
        cwPressureView.setText("");
        cwCloudyView.setImageDrawable(null);

        restartLoaders();
    }
}
