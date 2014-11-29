package ru.ifmo.md.lesson8.DataClasses;

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
    // only forecast
    public final String cloudyAM;
    public final String cloudyPM;
    public final int tempLow;
    public final int tempHigh;

    // current weather constructor
    Weather(String city, String country, String date, String time, String cloudy, int temp) {
        this.city = city;
        this.country = country;
        this.date = date;
        this.time = time;
        this.cloudy = cloudy;
        this.temp = temp;

        cloudyAM = null;
        cloudyPM = null;
        tempLow = 0;
        tempHigh = 0;
    }

    // forecast constructor
    Weather(String city, String country, String date, String cloudyAM, String cloudyPM,
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
    }
}
