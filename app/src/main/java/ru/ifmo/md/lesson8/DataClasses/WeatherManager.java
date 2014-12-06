package ru.ifmo.md.lesson8.DataClasses;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import ru.ifmo.md.lesson8.R;

/**
 * Created by german on 30.11.14.
 */
public class WeatherManager {
    /**
     * @return
     *      -1 if city doesn't exist
     *      _id in CitiesTable of this city otherwise
     */
    public static int getCityId(ContentResolver resolver, String city, String country) {
        Cursor c = resolver.query(
                WeatherContentProvider.CITIES_CONTENT,
                new String[]{
                        WeatherContentProvider.CITY_ID
                },
                WeatherContentProvider.CITY_NAME + " = ? AND "
                        + WeatherContentProvider.COUNTRY_NAME + " = ? ",
                new String[]{
                        city, country
                },
                null);
        int cityId;
        if (c.getCount() == 0) {
            cityId = -1;
        } else {
            c.moveToFirst();
            cityId = c.getInt(c.getColumnIndexOrThrow(WeatherContentProvider.CITY_ID));
        }
        c.close();
        return cityId;
    }

    /**
     * @return _id of city in CitiesTable
     */
    public static int addCity(ContentResolver resolver, String city, String country, String important) {
        assert(important.equals(WeatherContentProvider.isImportant)
                || important.equals(WeatherContentProvider.isNotImportant));
        int cityId = getCityId(resolver, city, country);
        if (cityId == -1) {
            ContentValues cv = new ContentValues();
            cv.put(WeatherContentProvider.CITY_NAME, city);
            cv.put(WeatherContentProvider.COUNTRY_NAME, country);
            cv.put(WeatherContentProvider.IS_IMPORTANT, important);
            Uri uri = resolver.insert(WeatherContentProvider.CITIES_CONTENT, cv);
            cityId = Integer.parseInt(uri.getLastPathSegment());
        }
        return cityId;
    }

    /**
     * @return _id of city in CitiesTable
     */
    public static int addCity(ContentResolver resolver, String city, String country) {
        int cityId = getCityId(resolver, city, country);
        if (cityId == -1) {
            ContentValues cv = new ContentValues();
            cv.put(WeatherContentProvider.CITY_NAME, city);
            cv.put(WeatherContentProvider.COUNTRY_NAME, country);
            cv.put(WeatherContentProvider.IS_IMPORTANT, WeatherContentProvider.isNotImportant);
            Uri uri = resolver.insert(WeatherContentProvider.CITIES_CONTENT, cv);
            cityId = Integer.parseInt(uri.getLastPathSegment());
        }
        return cityId;
    }

    public static void setCurrentWeather(ContentResolver resolver, Weather weather) {
        deleteCurWeatherByCity(resolver, weather.city, weather.country);
        // Add city in CitiesTable
        int cityId = addCity(resolver, weather.city, weather.country);
        // Create values to insert
        ContentValues curWeather = weather.toCurWeatherContentValues(cityId);
        Uri uri = resolver.insert(WeatherContentProvider.CUR_WEATHER_CONTENT, curWeather);
        System.out.println("inserted curweather in " + weather.city + " " + weather.country + " " + uri);
    }

    public static void addForecast(ContentResolver resolver, Weather weather) {
        // Add city in CitiesTable
        int cityId = addCity(resolver, weather.city, weather.country);
        ContentValues forecast = weather.toForecastContentValues(cityId);

        // Check if forecast was already added
        Cursor c = resolver.query(
                WeatherContentProvider.FORECAST_CONTENT,
                new String[] {
                        WeatherContentProvider.FORECAST_ID
                },
                WeatherContentProvider.FORECAST_CITY_ID + " = ? AND "
                + WeatherContentProvider.FORECAST_DATE + " = ? ",
                new String[] {
                        Integer.toString(cityId),
                        weather.date
                },
                null);
        if (c.getCount() == 0) {
            // It didn't
            Uri uri = resolver.insert(WeatherContentProvider.FORECAST_CONTENT, forecast);
            System.out.println("inserted forecast in " + weather.city + " " + weather.country + " " + uri);
        } else {
            c.moveToFirst();
            int forecastId = c.getInt(c.getColumnIndexOrThrow(WeatherContentProvider.FORECAST_ID));
            // It did, update information
            int updated = resolver.update(
                    WeatherContentProvider.FORECAST_CONTENT,
                    forecast,
                    WeatherContentProvider.FORECAST_ID + " = ? ",
                    new String[] {
                            Integer.toString(forecastId)
                    }
            );
            System.out.println("updated forecast in " + weather.city + " " + weather.country + " " + updated);
        }
    }

    public static Cursor getForecastByCity(ContentResolver resolver, String city, String country) {
        if (city == null || country == null) {
            return null;
        }
        int cityId = getCityId(resolver, city, country);
        Cursor c = resolver.query(
                WeatherContentProvider.FORECAST_CONTENT,
                null,
                WeatherContentProvider.CUR_WEATHER_CITY_ID + " = ? ",
                new String[] {
                        String.valueOf(cityId)
                },
                null
        );
        System.out.println("got forecast from " + city + " " + country + " " + c.getCount());
        return c;
    }

    public static void deleteForecastByCity(ContentResolver resolver, String city, String country) {
        if (city == null || country == null) {
            return;
        }
        int cityId = getCityId(resolver, city, country);
        int deleted = resolver.delete(
                WeatherContentProvider.FORECAST_CONTENT,
                WeatherContentProvider.FORECAST_CITY_ID + " = ? ",
                new String[] {
                        String.valueOf(cityId)
                }
        );
        System.out.println("deleted dorecast from " + city + " " + country + " " + deleted);
    }

    public static void deleteCurWeatherByCity(ContentResolver resolver, String city, String country) {
        if (city == null || country == null) {
            return;
        }
        int cityId = getCityId(resolver, city, country);
        int deleted = resolver.delete(
                WeatherContentProvider.CUR_WEATHER_CONTENT,
                WeatherContentProvider.CUR_WEATHER_CITY_ID + " = ? ",
                new String[] {
                        String.valueOf(cityId)
                }
        );
        System.out.println("deleted curweather from " + city + " " + country + " " + deleted);
    }

    public static void setImportant(ContentResolver resolver, String city, String country, String important) {
        assert(important.equals(WeatherContentProvider.isImportant)
                || important.equals(WeatherContentProvider.isNotImportant));
        int cityId = getCityId(resolver, city, country);
        if (cityId == -1) {
            System.out.println("It's a new city");
            addCity(resolver, city, country, important);
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put(WeatherContentProvider.CITY_NAME, city);
        cv.put(WeatherContentProvider.COUNTRY_NAME, country);
        cv.put(WeatherContentProvider.IS_IMPORTANT, important);
        int updated = resolver.update(
                WeatherContentProvider.CITIES_CONTENT,
                cv,
                WeatherContentProvider.CITY_ID + " = ? ",
                new String[] {
                        String.valueOf(cityId)
                }
        );
        System.out.println("Set important, udated: " + updated);
    }

    public static int getCloudyId(int code) {
//        13, 14, 15, 16 -- snow
//        41, 42, 43, 46 -- snow_showers
//        35, 6 -- rain_and_hail
//        11, 12, 39, 40 -- showers
//        5 -- rain_and_snow
//        10 -- freezing rain
//        31, 33 -- fair_night
//        32, 34 -- fair_day
//        26 -- cloudy
//        27 -- mostly_cloudy_night
//        28 -- mostly_cloudy_day
//        29 -- partly_cloudy_night
//        30 -- partly_cloudy_day
//        20, 22 -- foggy (smoky)
//        3, 4, 37, 38, 39 -- thunderstorms
//        36 -- hot
//        24 -- windy

        // 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
        // 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
        // 40, 41, 42, 43, 44, 45, 46, 47

        switch (code) {
            case 13:case 14:case 15:case 16:
                return R.drawable.snow;
            case 41:case 42:case 43:case 46:
                return R.drawable.snow_showers;
            case 35:case 6:
                return R.drawable.rain_and_hail;
            case 11:case 12:case 39:case 40:
                return R.drawable.showers;
            case 5:
                return R.drawable.rain_and_snow;
            case 10:
                return R.drawable.freezing_rain;
            case 31:case 33:
                return R.drawable.fair_night;
            case 32:case 34:
                return R.drawable.fair_day;
            case 24:
                return R.drawable.windy;
            case 26:
                return R.drawable.cloudy;
            case 27:
                return R.drawable.mostly_cloudy_night;
            case 28:
                return R.drawable.mostly_cloudy_day;
            case 29:
                return R.drawable.partly_cloudy_night;
            case 30:
                return R.drawable.partly_cloudy_day;
            case 20:case 22:
                return R.drawable.foggy;
            case 3:case 4:case 37:case 38:
                return R.drawable.thunderstorms;
            case 36:
                return R.drawable.hot;
            default:
                System.out.println("Unknown weather code: " + code);
                return R.drawable.ic_launcher;
        }
    }

    public static int toCelsius(int fahrTemp) {
        double dCelsTemp = (fahrTemp - 32.0) / 1.8;
        return (int) dCelsTemp;
    }
}
