package ru.ifmo.md.lesson8.DataClasses;

/**
 * Created by german on 06.12.14.
 */
public class City {
    public static final String WOEID_BUNDLE_KEY = "woeid";
    public static final String CITY_BUNDLE_KEY = "city";
    public static final String COUNTRY_BUNDLE_KEY = "country";

    private final String cityName;
    private final String countryName;
    private final int woeid;

    public City(String cityName, String countryName, int woeid) {
        this.cityName = cityName;
        this.countryName = countryName;
        this.woeid = woeid;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public int getWoeid() {
        return woeid;
    }

    public String toString() {
        return cityName + ", " + countryName + ", " + woeid;
    }
}
