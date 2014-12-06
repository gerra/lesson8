package ru.ifmo.md.lesson8.WeatherLoaderClasses;

import android.app.IntentService;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by german on 30.11.14.
 */
public class WeatherLoader extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public WeatherLoader() {
        super("Weather loader");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int woeid = intent.getIntExtra("woeid", 0);

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

        System.out.println(request);

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
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
