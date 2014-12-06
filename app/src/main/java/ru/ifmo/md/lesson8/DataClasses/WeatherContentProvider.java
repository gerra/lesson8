package ru.ifmo.md.lesson8.DataClasses;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by german on 29.11.14.
 */
public class WeatherContentProvider extends ContentProvider {
    /**
     * Database constants
     */
    private SQLiteDatabase db;

    private static final String DB_NAME = "WeatherDB";
    private static final int DB_VERSION = 1;

    // Cities
    private static final String CITIES_TABLE = "cities";

    public static final String CITY_ID = "_id";
    public static final String CITY_NAME = "city";
    public static final String COUNTRY_NAME = "country";
    public static final String IS_IMPORTANT = "important";
    public static final String WOEID = "woeid";

    public static final String isImportant = "Y";
    public static final String isNotImportant = "N";

    private static final String CITIES_TABLE_CREATE = "CREATE TABLE " + CITIES_TABLE + " ("
            + CITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CITY_NAME + " TEXT, "
            + COUNTRY_NAME + " TEXT, "
            + WOEID + " INTEGER, "
            + IS_IMPORTANT + " TEXT"
            + ");";

    // Current weather table
    private static final String CUR_WEATHER_TABLE = "current_weather";

    public static final String CUR_WEATHER_ID = "_id";
    public static final String CUR_WEATHER_CITY_ID = "city_id";
    public static final String CUR_WEATHER_DATE = "date";
    public static final String CUR_WEATHER_TIME = "time";
    public static final String CUR_WEATHER_CODE = "code";
    public static final String CUR_WEATHER_TEMP = "temp";
    public static final String CUR_WEATHER_WIND = "wind";
    public static final String CUR_WEATHER_HUMIDITY = "humidity";
    public static final String CUR_WEATHER_PRESSURE = "pressure";

    private static final String CUR_WEATHER_TABLE_CREATE = "CREATE TABLE " + CUR_WEATHER_TABLE + " ("
            + CUR_WEATHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CUR_WEATHER_CITY_ID + " INTEGER, "
            + CUR_WEATHER_DATE + " TEXT, "
            + CUR_WEATHER_TIME + " TEXT, "
            + CUR_WEATHER_CODE + " INTEGER, "
            + CUR_WEATHER_TEMP + " INTEGER, "
            + CUR_WEATHER_WIND + " TEXT, "
            + CUR_WEATHER_HUMIDITY + " TEXT, "
            + CUR_WEATHER_PRESSURE + " TEXT"
            + ");";
    // Forecast weather table
    private static final String FORECAST_TABLE = "forecast";

    public static final String FORECAST_ID = "_id";
    public static final String FORECAST_CITY_ID = "city_id";
    public static final String FORECAST_DATE = "date";
    public static final String FORECAST_CODE = "cloudy_pm";
    public static final String FORECAST_TEMP_LOW = "temp_low";
    public static final String FORECAST_TEMP_HIGH = "temp_high";

    private static final String FORECAST_TABLE_CREATE = "CREATE TABLE " + FORECAST_TABLE + " ("
            + FORECAST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FORECAST_CITY_ID + " INTEGER, "
            + FORECAST_DATE + " TEXT, "
            + FORECAST_CODE + " INTEGER, "
            + FORECAST_TEMP_LOW + " INTEGER, "
            + FORECAST_TEMP_HIGH + " INTEGER" + ");";
    /**
     * Provider constants
     */
    public static final String AUTHORITY = "ru.android.german.lesson8";

    public static final Uri DATA_CONTENT = Uri.parse("content://" + AUTHORITY);
    public static final Uri CITIES_CONTENT = Uri.parse("content://" + AUTHORITY +
            "/" + CITIES_TABLE);
    public static final Uri CUR_WEATHER_CONTENT = Uri.parse("content://" + AUTHORITY +
            "/" + CUR_WEATHER_TABLE);
    public static final Uri FORECAST_CONTENT = Uri.parse("content://" + AUTHORITY +
            "/" + FORECAST_TABLE);


    // constants for uri matching (u = uri)
    private static final int uCITIES = 1;
    private static final int uCITIES_ID = 2;
    private static final int uCUR_WEATHER = 5;
    private static final int uCUR_WEATHER_ID = 6;
    private static final int uFORECAST = 7;
    private static final int uFORECAST_ID = 8;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CITIES_TABLE, 1);
        uriMatcher.addURI(AUTHORITY, CITIES_TABLE + "/#", 2);
        uriMatcher.addURI(AUTHORITY, CUR_WEATHER_TABLE, 5);
        uriMatcher.addURI(AUTHORITY, CUR_WEATHER_TABLE + "/#", 6);
        uriMatcher.addURI(AUTHORITY, FORECAST_TABLE, 7);
        uriMatcher.addURI(AUTHORITY, FORECAST_TABLE + "/#", 8);
    }

    private static HashMap<String, String> PROJECTION_MAP;

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
            db.execSQL(CUR_WEATHER_TABLE_CREATE);
            db.execSQL(FORECAST_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + CITIES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS" + CUR_WEATHER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS" + CUR_WEATHER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS" + FORECAST_TABLE);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db != null);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case uCITIES:
                qb.setTables(CITIES_TABLE);
                qb.setProjectionMap(PROJECTION_MAP);
                break;
            case uCITIES_ID:
                qb.setTables(CITIES_TABLE);
                qb.appendWhere(CITY_ID + "=" + uri.getPathSegments().get(1));
                break;
            case uCUR_WEATHER:
                qb.setTables(CUR_WEATHER_TABLE);
                qb.setProjectionMap(PROJECTION_MAP);
                break;
            case uCUR_WEATHER_ID:
                qb.setTables(CUR_WEATHER_TABLE);
                qb.appendWhere(CUR_WEATHER_ID + "=" + uri.getPathSegments().get(1));
                break;
            case uFORECAST:
                qb.setTables(FORECAST_TABLE);
                qb.setProjectionMap(PROJECTION_MAP);
                break;
            case uFORECAST_ID:
                qb.setTables(FORECAST_TABLE);
                qb.appendWhere(FORECAST_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case uCITIES:
                long rowID = db.insert(CITIES_TABLE, null, values);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(CITIES_CONTENT, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                throw new SQLException("City inserting error: Failed insert values to " + uri);
            case uCUR_WEATHER:
                rowID = db.insert(CUR_WEATHER_TABLE, null, values);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(CUR_WEATHER_CONTENT, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                throw new SQLException("Current weather inserting error: Failed insert values to " + uri);
            case uFORECAST:
                rowID = db.insert(FORECAST_TABLE, null, values);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(FORECAST_CONTENT, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                throw new SQLException("Forecast inserting error: Failed insert values to " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case uCITIES:
                count = db.delete(CITIES_TABLE, selection, selectionArgs);
                break;
            case uCITIES_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(CITIES_TABLE, CITY_ID + " = " + id
                        + (TextUtils.isEmpty(selection) ? "" : " AND ( " + selection + ")"),
                        selectionArgs);
                break;
            case uCUR_WEATHER:
                count = db.delete(CUR_WEATHER_TABLE, selection, selectionArgs);
                break;
            case uCUR_WEATHER_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(CUR_WEATHER_TABLE, CUR_WEATHER_ID + " = " + id
                        + (TextUtils.isEmpty(selection) ? "" : " AND ( " + selection + ")"),
                        selectionArgs);
                break;
            case uFORECAST:
                count = db.delete(FORECAST_TABLE, selection, selectionArgs);
                break;
            case uFORECAST_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(FORECAST_TABLE, FORECAST_ID + " = " + id
                        + (TextUtils.isEmpty(selection) ? "" : " AND ( " + selection + ")"),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case uCITIES:
                count = db.update(CITIES_TABLE, values, selection, selectionArgs);
                break;
            case uCITIES_ID:
                String id = uri.getPathSegments().get(1);
                count = db.update(CITIES_TABLE, values, CITY_ID + " = " + id
                                + (TextUtils.isEmpty(selection) ? "" : " AND ( " + selection + ")"),
                        selectionArgs);
                break;
            case uCUR_WEATHER:
                count = db.update(CUR_WEATHER_TABLE, values, selection, selectionArgs);
                break;
            case uCUR_WEATHER_ID:
                id = uri.getPathSegments().get(1);
                count = db.update(CUR_WEATHER_TABLE, values, CUR_WEATHER_ID + " = " + id
                                + (TextUtils.isEmpty(selection) ? "" : " AND ( " + selection + ")"),
                        selectionArgs);
                break;
            case uFORECAST:
                count = db.update(FORECAST_TABLE, values, selection, selectionArgs);
                break;
            case uFORECAST_ID:
                id = uri.getPathSegments().get(1);
                count = db.update(FORECAST_TABLE, values, FORECAST_ID + " = " + id
                                + (TextUtils.isEmpty(selection) ? "" : " AND ( " + selection + ")"),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
