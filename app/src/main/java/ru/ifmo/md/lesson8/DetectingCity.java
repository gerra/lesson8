package ru.ifmo.md.lesson8;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import ru.ifmo.md.lesson8.DataClasses.City;

class DetectingCity extends AsyncTask<Double, Void, City> {
    private String TAG = getClass().getName();

    private class XMLParser {
        String city;
        String country;
        int woeid;
        City parse(InputStream in) {
            RootElement root = new RootElement("query");
            Element results = root.getChild("results");
            Element result = results.getChild("Result");

            Element cityElement = result.getChild("city");
            Element countryElement = result.getChild("country");
            Element woeidElement = result.getChild("woeid");

            woeidElement.setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {woeid = Integer.parseInt(body);
                }
            });
            countryElement.setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {country = body;
                }
            });
            cityElement.setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    city = body;
                }
            });

            result.setStartElementListener(new StartElementListener() {
                @Override
                public void start(Attributes attributes) {
                    city = null;
                    country = null;
                    woeid = 0;
                }
            });

            try {
                Xml.parse(in, Xml.Encoding.UTF_8, root.getContentHandler());
                if (city != null && country != null && woeid != 0) {
                    Log.d(TAG, "Detected " + city + " " + country + " " + woeid);
                    return new City(city, country, woeid);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private Context context;

    public DetectingCity(Context context) {
        this.context = context;
    }

    @Override
    protected City doInBackground(Double... params) {
        String location = String.valueOf(params[0]) + ", " + String.valueOf(params[1]);
        String query = "select * from geo.placefinder where text=\"" + location +"\" and gflags=\"R\"";
        try {
            String strUrl = "https://query.yahooapis.com/v1/public/yql?q=" + URLEncoder.encode(query, "UTF-8") + "&format=xml";
            Log.i(TAG, "Querying to server with query " + query + " (url = " + strUrl + ")");

            URL url = new URL(strUrl);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                return (new XMLParser()).parse(in);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(City city) {
        super.onPostExecute(city);
        if (city != null) {
            Log.d(TAG, city.toString());
        } else {
            Toast.makeText(context, "Unable to detect city", Toast.LENGTH_SHORT).show();
        }
    }
}