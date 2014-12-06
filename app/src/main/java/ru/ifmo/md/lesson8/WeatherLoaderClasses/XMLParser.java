package ru.ifmo.md.lesson8.WeatherLoaderClasses;

import android.content.Context;
import android.sax.Element;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.ifmo.md.lesson8.DataClasses.City;
import ru.ifmo.md.lesson8.DataClasses.Weather;
import ru.ifmo.md.lesson8.DataClasses.WeatherManager;

/**
 * Created by german on 01.12.14.
 */
public class XMLParser extends DefaultHandler {
    private static String namespace = "http://xml.weather.yahoo.com/ns/rss/1.0";
    private static Locale locale = Locale.US;

    private final Context context;
    private final int woeid;

    public XMLParser(Context context, int woeid) {
        this.context = context;
        this.woeid = woeid;
    }

    private City city;
    private boolean isFahrenheit;

    private String curWind="";
    private String curHumidity="";
    private String curPressure="";

    public void parse(InputStream in) {
        RootElement root = new RootElement("query");
        Element results = root.getChild("results");
        Element channel = results.getChild("channel");

        Element units = channel.getChild(namespace, "units");
        units.setStartElementListener(new StartElementListener() {
            @Override
            public void start(Attributes attributes) {
                isFahrenheit = attributes.getValue("temperature").equals("F");
            }
        });

        Element wind = channel.getChild(namespace, "wind");
        wind.setStartElementListener(new StartElementListener() {
            @Override
            public void start(Attributes attributes) {
                curWind = attributes.getValue("speed")
                        + "mph, " + attributes.getValue("direction") + "°";
            }
        });

        Element atmosphere = channel.getChild(namespace, "atmosphere");
        atmosphere.setStartElementListener(new StartElementListener() {
            @Override
            public void start(Attributes attributes) {
                curHumidity = attributes.getValue("humidity") + "%";
                curPressure = attributes.getValue("pressure") + "in";
            }
        });

        Element location = channel.getChild(namespace, "location");
        location.setStartElementListener(new StartElementListener() {
            @Override
            public void start(Attributes attributes) {
                city = new City(attributes.getValue("city"), attributes.getValue("country"), woeid);
                WeatherManager.deleteForecastByCity(context.getContentResolver(), city);
            }
        });

        Element item = channel.getChild("item");
        Element curWeather = item.getChild(namespace, "condition");
        curWeather.setStartElementListener(new StartElementListener() {
            @Override
            public void start(Attributes attributes) {
                //Tue, 02 Dec 2014 2:53 am
                String date = "??.??";
                String time = "??:??";
                int code = -1;
                int temp = 0;

                try {
                    SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy h:mm a", locale);
                    Date weatherDate;
                    Calendar calendar;
                    String readDate = attributes.getValue("date");
                    readDate = readDate.replace("am", "AM").replace("pm","PM");
                    weatherDate = df.parse(readDate);

                    System.out.println(weatherDate);

                    calendar = Calendar.getInstance();
                    calendar.setTime(weatherDate);

                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    date = Integer.toString(day) + "." + Integer.toString(month);

                    int minutes = calendar.get(Calendar.MINUTE);
                    int hours = calendar.get(Calendar.HOUR_OF_DAY);

                    time = String.format("%02d", hours) + ":" + String.format("%02d", minutes);
                    // timeZone:
                    String[] dateComponents = readDate.split(" ");
                    if (dateComponents.length == 7) {
                        time += "\n(" + dateComponents[6] + ")";
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // temperature
                temp = Integer.parseInt(attributes.getValue("temp"));
                if (isFahrenheit) {
                    temp = WeatherManager.toCelsius(temp);
                }
                // cloudy
                code = Integer.parseInt(attributes.getValue("code"));
                WeatherManager.setCurrentWeather(
                        context.getContentResolver(),
                        new Weather(
                                city,
                                date, time,
                                code, temp,
                                curWind, curHumidity, curPressure
                        ));
            }
        });

        Element forecast = item.getChild(namespace, "forecast");
        forecast.setStartElementListener(new StartElementListener() {
            @Override
            public void start(Attributes attributes) {
                // 2 Dec 2014
                String date = "??.??";
                int code = -1;
                int tempLow = 0;
                int tempHigh = 0;
                try {
                    SimpleDateFormat df = new SimpleDateFormat("d MMM yyyy", locale);
                    String readDate = attributes.getValue("date");
                    Date weatherDate = df.parse(readDate);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(weatherDate);

                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    date = Integer.toString(day) + "." + Integer.toString(month);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // temperature
                tempLow = Integer.parseInt(attributes.getValue("low"));
                tempHigh = Integer.parseInt(attributes.getValue("high"));
                if (isFahrenheit) {
                    tempLow = WeatherManager.toCelsius(tempLow);
                    tempHigh = WeatherManager.toCelsius(tempHigh);
                }
                // cloudy
                code = Integer.parseInt(attributes.getValue("code"));

                WeatherManager.addForecast(
                        context.getContentResolver(),
                        new Weather(
                                city, date,
                                code, tempLow, tempHigh
                        )
                );
            }
        });

        try {
            Xml.parse(in, Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
