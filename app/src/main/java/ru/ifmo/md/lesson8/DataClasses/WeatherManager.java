package ru.ifmo.md.lesson8.DataClasses;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
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
    private static int getCityId(ContentResolver resolver, String city, String country) {
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
     * @return
     *      -1 if pair (cityId, date) doesn't exist
     *      _id in AllWeatherTable otherwise
     */
    public static int getAllWeatherId(ContentResolver resolver, int cityId, String date) {
        Cursor c = resolver.query(
                WeatherContentProvider.ALL_WEATHER_CONTENT,
                new String[]{
                        WeatherContentProvider.ALL_WEATHER_ID
                },
                WeatherContentProvider.ALL_WEATHER_CITY_ID + " = ? AND "
                        + WeatherContentProvider.ALL_WEATHER_DATE + " = ? ",
                new String[] {
                        Integer.toString(cityId), date
                },
                null);
        int allWeatherId;
        if (c.getCount() == 0) {
            allWeatherId = -1;
        } else {
            c.moveToFirst();
            allWeatherId = c.getInt(c.getColumnIndexOrThrow(WeatherContentProvider.ALL_WEATHER_ID));
        }
        c.close();
        return allWeatherId;
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
            Uri uri = resolver.insert(WeatherContentProvider.CITIES_CONTENT, cv);
            cityId = Integer.parseInt(uri.getLastPathSegment());
        }
        return cityId;
    }

    /**
     * @return _id of weather in AllWeatherTable
     */
    private static int addAllWeather(ContentResolver resolver, int cityId, String date) {
        int allWeatherId = getAllWeatherId(resolver, cityId, date);
        if (allWeatherId == -1) {
            ContentValues cv = new ContentValues();
            cv.put(WeatherContentProvider.ALL_WEATHER_CITY_ID, cityId);
            cv.put(WeatherContentProvider.ALL_WEATHER_DATE, date);
            Uri uri = resolver.insert(WeatherContentProvider.ALL_WEATHER_CONTENT, cv);
            allWeatherId = Integer.parseInt(uri.getLastPathSegment());
        }
        return allWeatherId;
    }

    public static void setCurrentWeather(ContentResolver resolver, Weather weather) {
        // Create values to insert
        ContentValues curWeather = new ContentValues();
        curWeather.put(WeatherContentProvider.CUR_WEATHER_CLOUDY, weather.cloudy);
        curWeather.put(WeatherContentProvider.CUR_WEATHER_TEMP, weather.temp);
        curWeather.put(WeatherContentProvider.CUR_WEATHER_TIME, weather.time);
        curWeather.put(WeatherContentProvider.CUR_WEATHER_WIND, weather.wind);
        curWeather.put(WeatherContentProvider.CUR_WEATHER_HUMIDITY, weather.humidity);
        curWeather.put(WeatherContentProvider.CUR_WEATHER_PRESSURE, weather.pressure);

        // Add city in CitiesTable
        int cityId = addCity(resolver, weather.city, weather.country);

        // Add weather by cityId and date in AllWeatherTable
        int allWeatherId = addAllWeather(resolver, cityId, weather.date);

        // Insert or update curWeather in CurWeatherTable
        curWeather.put(WeatherContentProvider.CUR_WEATHER_ALL_ID, allWeatherId);

        // Check if weather was already added
        Cursor c = resolver.query(
                WeatherContentProvider.CUR_WEATHER_CONTENT,
                new String[] {
                        WeatherContentProvider.CUR_WEATHER_ID
                },
                WeatherContentProvider.CUR_WEATHER_ALL_ID + " = ? ",
                new String[] {
                        Integer.toString(allWeatherId)
                },
                null);
        if (c.getCount() == 0) {
            // It didn't
            resolver.insert(WeatherContentProvider.CUR_WEATHER_CONTENT, curWeather);
        } else {
            // It did, update information
            resolver.update(
                    WeatherContentProvider.CUR_WEATHER_CONTENT,
                    curWeather,
                    WeatherContentProvider.CUR_WEATHER_ALL_ID + " = ? ",
                    new String[] {
                            Integer.toString(allWeatherId)
                    }
            );
        }
    }

    public static void addForecast(ContentResolver resolver, Weather weather) {
        ContentValues forecast = new ContentValues();
        forecast.put(WeatherContentProvider.FORECAST_CLOUDY_AM, weather.cloudyAM);
        forecast.put(WeatherContentProvider.FORECAST_CLOUDY_PM, weather.cloudyPM);
        forecast.put(WeatherContentProvider.FORECAST_TEMP_LOW, weather.tempLow);
        forecast.put(WeatherContentProvider.FORECAST_TEMP_HIGH, weather.tempHigh);

        // Add city in CitiesTable
        int cityId = addCity(resolver, weather.city, weather.country);

        // Add weather by cityId and date in AllWeatherTable
        int allWeatherId = addAllWeather(resolver, cityId, weather.date);

        // Insert or update forecast in ForecastTable
        forecast.put(WeatherContentProvider.FORECAST_ALL_ID, allWeatherId);

        // Check if forecast was already added
        Cursor c = resolver.query(
                WeatherContentProvider.FORECAST_CONTENT,
                new String[] {
                        WeatherContentProvider.FORECAST_ID
                },
                WeatherContentProvider.FORECAST_ALL_ID + " = ? ",
                new String[] {
                        Integer.toString(allWeatherId)
                },
                null);
        if (c.getCount() == 0) {
            // It didn't
            resolver.insert(WeatherContentProvider.FORECAST_CONTENT, forecast);
        } else {
            // It did, update information
            resolver.update(
                    WeatherContentProvider.FORECAST_CONTENT,
                    forecast,
                    WeatherContentProvider.FORECAST_ALL_ID + " = ? ",
                    new String[] {
                            Integer.toString(allWeatherId)
                    }
            );
        }
    }

    public static String[] getCityAndCountryByWeatherId(ContentResolver resolver, int allId) {
        Cursor c = resolver.query(
                WeatherContentProvider.ALL_WEATHER_CONTENT,
                new String[] {
                        WeatherContentProvider.ALL_WEATHER_CITY_ID
                },
                WeatherContentProvider.ALL_WEATHER_ID + " = ? ",
                new String[] {
                        Integer.toString(allId)
                }, null);
        if (c.getCount() == 0) {
            return null;
        }
        c.moveToFirst();
        int cityId = c.getInt(c.getColumnIndexOrThrow(WeatherContentProvider.ALL_WEATHER_CITY_ID));
        c.close();
        c = resolver.query(
                WeatherContentProvider.CITIES_CONTENT,
                new String[] {
                        WeatherContentProvider.CITY_NAME,
                        WeatherContentProvider.COUNTRY_NAME
                },
                WeatherContentProvider.CITY_ID + " = ? ",
                new String[] {
                        Integer.toString(cityId)
                }, null);
        c.moveToFirst();
        String[] res = new String[] {
                c.getString(c.getColumnIndexOrThrow(WeatherContentProvider.CITY_NAME)),
                c.getString(c.getColumnIndexOrThrow(WeatherContentProvider.COUNTRY_NAME))
        };
        c.close();
        return res;
    }

    public static String getDateByWeatherId(ContentResolver resolver, int allId) {
        Cursor c = resolver.query(
                WeatherContentProvider.ALL_WEATHER_CONTENT,
                new String[] {
                        WeatherContentProvider.ALL_WEATHER_DATE
                },
                WeatherContentProvider.ALL_WEATHER_ID + " = ? ",
                new String[] {
                        Integer.toString(allId)
                }, null);
        if (c.getCount() == 0) {
            return "";
        }
        c.moveToFirst();
        String res = c.getString(c.getColumnIndexOrThrow(WeatherContentProvider.ALL_WEATHER_DATE));
        c.close();
        return res;
    }

    public static Cursor getForecastByCity(ContentResolver resolver, String city, String country) {
        if (city == null || country == null) {
            return null;
        }
        Cursor cursor = resolver.query(
                WeatherContentProvider.CITIES_CONTENT,
                new String[] {
                        WeatherContentProvider.CITY_ID
                },
                WeatherContentProvider.CITY_NAME + " = ? AND " +
                        WeatherContentProvider.COUNTRY_NAME + " = ? ",
                new String[] {
                        city, country
                }, null
        );
        int cnt = cursor.getCount();
        if (cnt == 0) {
            cursor.close();
            return null;
        } else {
            String where = "";
            String ids[] = new String[cnt];
            cursor.moveToFirst();
            for (int i = 0; i < cnt; i++) {
                where += (i == 0 ?  "" : "OR ") + WeatherContentProvider.ALL_WEATHER_CITY_ID + " = ? ";
                ids[i] = Integer.toString(
                        cursor.getInt(cursor.getColumnIndexOrThrow(WeatherContentProvider.CITY_ID))
                );
                cursor.moveToNext();
            }
            cursor.close();
            cursor = resolver.query(
                    WeatherContentProvider.ALL_WEATHER_CONTENT,
                    new String[] {
                            WeatherContentProvider.ALL_WEATHER_ID
                    },
                    where, ids, null
            );
            cnt = cursor.getCount();
            if (cnt == 0) {
                return null;
            } else {
                where = "";
                ids = new String[cnt];
                cursor.moveToFirst();
                for (int i = 0; i < cnt; i++) {
                    where += (i == 0 ?  "" : "OR ") + WeatherContentProvider.FORECAST_ALL_ID + " = ? ";
                    ids[i] = Integer.toString(
                            cursor.getInt(cursor.getColumnIndexOrThrow(WeatherContentProvider.ALL_WEATHER_ID))
                    );
                    cursor.moveToNext();
                }
                cursor.close();
                cursor = resolver.query(
                        WeatherContentProvider.FORECAST_CONTENT,
                        null,
                        where, ids, null
                );
                return cursor;
            }
        }
    }

    public static void deleteForecastByCity(ContentResolver resolver, String city, String country) {
        if (city == null || country == null) {
            return;
        }
        Cursor cursor = resolver.query(
                WeatherContentProvider.CITIES_CONTENT,
                new String[] {
                        WeatherContentProvider.CITY_ID
                },
                WeatherContentProvider.CITY_NAME + " = ? AND " +
                        WeatherContentProvider.COUNTRY_NAME + " = ? ",
                new String[] {
                        city, country
                }, null
        );
        int cnt = cursor.getCount();
        if (cnt == 0) {
            cursor.close();
            return;
        } else {
            String where = "";
            String ids[] = new String[cnt];
            cursor.moveToFirst();
            for (int i = 0; i < cnt; i++) {
                where += (i == 0 ?  "" : "OR ") + WeatherContentProvider.ALL_WEATHER_CITY_ID + " = ? ";
                ids[i] = Integer.toString(
                        cursor.getInt(cursor.getColumnIndexOrThrow(WeatherContentProvider.CITY_ID))
                );
                cursor.moveToNext();
            }
            cursor.close();
            cursor = resolver.query(
                    WeatherContentProvider.ALL_WEATHER_CONTENT,
                    new String[] {
                            WeatherContentProvider.ALL_WEATHER_ID
                    },
                    where, ids, null
            );
            cnt = cursor.getCount();
            if (cnt == 0) {
                return;
            } else {
                where = "";
                ids = new String[cnt];
                cursor.moveToFirst();
                for (int i = 0; i < cnt; i++) {
                    where += (i == 0 ?  "" : "OR ") + WeatherContentProvider.FORECAST_ALL_ID + " = ? ";
                    ids[i] = Integer.toString(
                            cursor.getInt(cursor.getColumnIndexOrThrow(WeatherContentProvider.ALL_WEATHER_ID))
                    );
                    cursor.moveToNext();
                }
                cursor.close();
                int deleted = resolver.delete(
                        WeatherContentProvider.FORECAST_CONTENT,
                        where, ids
                );
                System.out.println("Deleted " + deleted + " forecasts");
            }
        }
    }

    public static int getCloudyId(String text) {
        if (text == null) {
            return R.drawable.ic_launcher;
        }
        int code = Integer.parseInt(text);
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
        double dFahrTemp = (double) fahrTemp;
        double dCelsTemp = (fahrTemp - 32.0) / 1.8;
        return (int) dCelsTemp;
    }
}
