package ru.ifmo.md.lesson8.DataClasses;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by german on 29.11.14.
 */
public class WeatherContentProvider extends ContentProvider {
    /**
     * Database constants
     */
    private SQLiteDatabase db;

    public SQLiteDatabase getDB() {
        return db;
    }

    private static final String DB_NAME = "WeatherDB";
    private static final int DB_VERSION = 1;

    // Cities
    private static final String CITIES_TABLE = "cities";

    public static final String CITY_ID = "_id";
    public static final String CITY_NAME = "city";
    public static final String COUNTRY_NAME = "country";

    private static final String CITIES_TABLE_CREATE = "CREATE TABLE " + CITIES_TABLE + " ("
            + CITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CITY_NAME + " TEXT, "
            + COUNTRY_NAME + " TEXT" + ");";
    // All weather table
    private static final String ALL_WEATHER_TABLE = "all_weather";

    public static final String ALL_WEATHER_ID = "_id";
    public static final String ALL_WEATHER_CITY_ID = "city_id";
    public static final String ALL_WEATHER_DATE = "date";

    private static final String ALL_WEATHER_TABLE_CREATE = "CREATE TABLE " + ALL_WEATHER_TABLE + " ("
            + ALL_WEATHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ALL_WEATHER_CITY_ID + " INTEGER, "
            + ALL_WEATHER_DATE + " TEXT" + ");";
    // Current weather table
    private static final String CUR_WEATHER_TABLE = "current_weather";

    public static final String CUR_WEATHER_ID = "_id";
    public static final String CUR_WEATHER_ALL_ID = "all_id";
    public static final String CUR_WEATHER_CLOUDY = "cloudy";
    public static final String CUR_WEATHER_TEMP = "temp";

    private static final String CUR_WEATHER_TABLE_CREATE = "CREATE TABLE " + CUR_WEATHER_TABLE + " ("
            + CUR_WEATHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CUR_WEATHER_ALL_ID + " INTEGER, "
            + CUR_WEATHER_CLOUDY + " TEXT, "
            + CUR_WEATHER_TEMP + " INTEGER" + ");";
    // Forecast weather table
    private static final String FORECAST_TABLE = "forecast";

    public static final String FORECAST_ID = "_id";
    public static final String FORECAST_ALL_ID = "all_id";
    public static final String FORECAST_CLOUDY_AM = "cloudy_am";
    public static final String FORECAST_CLOUDY_PM = "cloudy_pm";
    public static final String FORECAST_TEMP_LOW = "temp_low";
    public static final String FORECAST_TEMP_HIGH = "temp_high";

    private static final String FORECAST_TABLE_CREATE = "CREATE TABLE " + FORECAST_TABLE + " ("
            + FORECAST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FORECAST_ALL_ID + " INTEGER, "
            + FORECAST_CLOUDY_AM + " TEXT, "
            + FORECAST_CLOUDY_PM + " TEXT, "
            + FORECAST_TEMP_LOW + " INTEGER, "
            + FORECAST_TEMP_HIGH + " INTEGER" + ");";
    /**
     * Provider constants
     */
    public static final String AUTHORITY = "ru.android.german.lesson8";

    public static final Uri DATA_CONTENT = Uri.parse("content://" + AUTHORITY);
    public static final Uri CITIES_CONTENT = Uri.parse("content://" + AUTHORITY +
            "/" + CITIES_TABLE);
    public static final Uri ALL_WEATHER_CONTENT = Uri.parse("content://" + AUTHORITY +
            "/" + ALL_WEATHER_TABLE);
    public static final Uri CUR_WEATHER_CONTENT = Uri.parse("content://" + AUTHORITY +
            "/" + CUR_WEATHER_TABLE);
    public static final Uri FORECAST_CONTENT = Uri.parse("content://" + AUTHORITY +
            "/" + FORECAST_TABLE);


    // constants for uri matching (u = uri)
    private static final int uCITIES = 1;
    private static final int uCITIES_ID = 2;
    private static final int uALL_WEATHER = 3;
    private static final int uALL_WEATHER_ID = 4;
    private static final int uCUR_WEATHER = 5;
    private static final int uCUR_WEATHER_ID = 6;
    private static final int uFORECAST = 7;
    private static final int uFORECAST_ID = 8;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CITIES_TABLE, 1);
        uriMatcher.addURI(AUTHORITY, CITIES_TABLE + "/#", 2);
        uriMatcher.addURI(AUTHORITY, ALL_WEATHER_TABLE, 3);
        uriMatcher.addURI(AUTHORITY, ALL_WEATHER_TABLE + "/#", 4);
        uriMatcher.addURI(AUTHORITY, CUR_WEATHER_TABLE, 5);
        uriMatcher.addURI(AUTHORITY, CUR_WEATHER_TABLE + "/#", 6);
        uriMatcher.addURI(AUTHORITY, FORECAST_TABLE, 7);
        uriMatcher.addURI(AUTHORITY, FORECAST_TABLE + "/#", 8);
    }

    /**
     * Help class
     */
    private static class DataBaseHelper extends SQLiteOpenHelper {
        DataBaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CITIES_TABLE_CREATE);
            db.execSQL(ALL_WEATHER_TABLE_CREATE);
            db.execSQL(CUR_WEATHER_TABLE_CREATE);
            db.execSQL(FORECAST_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + CITIES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS" + ALL_WEATHER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS" + CUR_WEATHER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS" + CUR_WEATHER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS" + FORECAST_TABLE);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case (uCITIES):
                return 
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
