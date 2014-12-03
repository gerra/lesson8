package ru.ifmo.md.lesson8.DataClasses;

import android.content.ContentResolver;
import android.content.ContentValues;

/**
 * Created by german on 29.11.14.
 */
public class Weather {
    public final String city;
    public final String country;
    public final String date;
    public final int code;
    // only current
    public final int temp;
    public final String time;
    public final String wind;
    public final String humidity;
    public final String pressure;
    // only forecast
    public final int tempLow;
    public final int tempHigh;

    private boolean isInit = false;

    // current weather constructor
    public Weather(String city, String country, String date, String time, int code, int temp,
                   String wind, String humidity, String pressure) {
        this.city = city;
        this.country = country;
        this.date = date;
        this.time = time;
        this.code = code;
        this.temp = temp;
        this.wind = wind;
        this.humidity = humidity;
        this.pressure = pressure;

        tempLow = 0;
        tempHigh = 0;

        isInit = true;
    }

    // forecast constructor
    public Weather(String city, String country, String date, int code,
            int tempLow, int tempHigh) {
        this.city = city;
        this.country = country;
        this.date = date;
        this.code = code;
        this.tempLow = tempLow;
        this.tempHigh = tempHigh;

        temp = 0;
        time = null;
        wind = null;
        humidity = null;
        pressure = null;

        isInit = true;
    }

    public ContentValues toForecastContentValues(int cityId) {
        if (!isInit) {
            throw new RuntimeException("Forecast is not initialized");
        }
        ContentValues cv = new ContentValues();
        cv.put(WeatherContentProvider.FORECAST_CITY_ID, cityId);
        cv.put(WeatherContentProvider.FORECAST_CODE, code);
        cv.put(WeatherContentProvider.FORECAST_DATE, date);
        cv.put(WeatherContentProvider.FORECAST_TEMP_LOW, tempLow);
        cv.put(WeatherContentProvider.FORECAST_TEMP_HIGH, tempHigh);
        return cv;
    }

    public ContentValues toCurWeatherContentValues(int cityId) {
        if (!isInit) {
            throw new RuntimeException("Current weather is not initialized");
        }
        ContentValues cv = new ContentValues();
        cv.put(WeatherContentProvider.CUR_WEATHER_CITY_ID, cityId);
        cv.put(WeatherContentProvider.CUR_WEATHER_DATE, date);
        cv.put(WeatherContentProvider.CUR_WEATHER_TIME, time);
        cv.put(WeatherContentProvider.CUR_WEATHER_CODE, code);
        cv.put(WeatherContentProvider.CUR_WEATHER_TEMP, temp);
        cv.put(WeatherContentProvider.CUR_WEATHER_WIND, wind);
        cv.put(WeatherContentProvider.CUR_WEATHER_PRESSURE, pressure);
        cv.put(WeatherContentProvider.CUR_WEATHER_HUMIDITY, humidity);
        return cv;
    }
}
