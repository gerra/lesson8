package ru.ifmo.md.lesson8.WeatherLoaderClasses;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import ru.ifmo.md.lesson8.DataClasses.City;
import ru.ifmo.md.lesson8.R;

/**
 * Created by german on 30.11.14.
 */
public class WeatherLoader extends IntentService {
    private final String TAG = getClass().getName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public WeatherLoader() {
        super("Weather loader");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int woeid = intent.getIntExtra(City.WOEID_BUNDLE_KEY, 0);

        String request = "https://query.yahooapis.com/v1/public/yql?q=";
//        String select = new String("select * from weather.forecast where woeid in " +
//                "(select woeid from geo.places(1) where text=" + "\"" + cityForQuery + "\")");
        String select = "select * from weather.forecast where woeid = " + String.valueOf(woeid);
        try {
            request += URLEncoder.encode(select, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request += "&format=xml";

        URL url;
        try {
            url = new URL(request);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                XMLParser parser = new XMLParser(getApplicationContext(), woeid);
                parser.parse(in);
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_network, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Bad response code");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.error_network, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Bad url");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.error_network, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Bad input stream");
        }
    }
}
