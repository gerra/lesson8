package ru.ifmo.md.lesson8.CitiesLoaderClasses;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import ru.ifmo.md.lesson8.DataClasses.City;

/**
 * Created by german on 05.12.14.
 */
public class CitiesLoader {
    public static List<City> getCities(String pattern) {
        ArrayList<City> cities = new ArrayList<>();
        if (pattern != null) {
            String query = "select * from geo.places where text=" + "\"" + pattern + "\"";
            try {
                String strUrl = "https://query.yahooapis.com/v1/public/yql?q=" + URLEncoder.encode(query, "UTF-8") + "&format=xml";

                System.out.println(strUrl);

                URL url = new URL(strUrl);
                URLConnection connection = url.openConnection();
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                int responseCode = httpConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = httpConnection.getInputStream();
                    (new XMLParser()).parse(in, cities);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cities;
    }

    public static class XMLParser extends DefaultHandler {
        private String city;
        private String country;
        private int woeid;
        private static final String namespace = "http://where.yahooapis.com/v1/schema.rng";

        public void parse(InputStream in, final ArrayList<City> cities) {
            RootElement root = new RootElement("query");
            Element results = root.getChild("results");
            Element place = results.getChild(namespace, "place");
            Element woeidElement = place.getChild(namespace, "woeid");
            Element countryElement = place.getChild(namespace, "country");
            Element locality1 = place.getChild(namespace, "locality1");

            woeidElement.setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) { woeid = Integer.parseInt(body);
                }
            });
            countryElement.setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {country = body;
                }
            });
            locality1.setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    city = body;
                }
            });

            place.setEndElementListener(new EndElementListener() {
                @Override
                public void end() {
                    if (city != null && country != null && !city.equals("") && !country.equals("") && woeid != -1) {
                        City newCity = new City(city, country, woeid);
                        Log.i("Cities loader", "Added " + newCity.toString() + " to dropdown list");
                        cities.add(newCity);
                    }
                    city = null;
                    country = null;
                    woeid = -1;
                }
            });

            try {
                Xml.parse(in, Xml.Encoding.UTF_8, root.getContentHandler());
                System.out.println(cities.size());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }
    }
}
