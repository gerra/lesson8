package ru.ifmo.md.lesson8.DataClasses;

import android.content.ContentResolver;
import android.content.ContentValues;

/**
 * Created by german on 29.11.14.
 */
public class Weather {
    // all weather
    public final String city;
    public final String country;
    public final String date;
    // only current
    public final String cloudy;
    public final int temp;
    public final String time;
    public final String wind;
    public final String humidity;
    public final String pressure;
    // only forecast
    public final String cloudyAM;
    public final String cloudyPM;
    public final int tempLow;
    public final int tempHigh;

    // current weather constructor
    public Weather(String city, String country, String date, String time, String cloudy, int temp,
                   String wind, String humidity, String pressure) {
        this.city = city;
        this.country = country;
        this.date = date;
        this.time = time;
        this.cloudy = cloudy;
        this.temp = temp;
        this.wind = wind;
        this.humidity = humidity;
        this.pressure = pressure;

        cloudyAM = null;
        cloudyPM = null;
        tempLow = 0;
        tempHigh = 0;
    }

    // forecast constructor
    public Weather(String city, String country, String date, String cloudyAM, String cloudyPM,
            int tempLow, int tempHigh) {
        this.city = city;
        this.country = country;
        this.date = date;
        this.cloudyAM = cloudyAM;
        this.cloudyPM = cloudyPM;
        this.tempLow = tempLow;
        this.tempHigh = tempHigh;

        cloudy = null;
        temp = 0;
        time = null;
        this.wind = null;
        this.humidity = null;
        this.pressure = null;
    }

//    public ContentValues getCWContentValues() {
//        ContentValues cv = new ContentValues();
//    }
}
