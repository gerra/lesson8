package ru.ifmo.md.lesson8;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import ru.ifmo.md.lesson8.DataClasses.WeatherManager;

public class UpdateService extends IntentService {
    private final String TAG = getClass().getName();

    public UpdateService() {
        super("Update Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Starting automatic updating");
        WeatherManager.refresh(getBaseContext());
        ArrayList<Integer> woeids = WeatherManager.getImportantCitiesWoeids(getContentResolver());
        int curCityWoeid = WeatherManager.getCurCity().getWoeid();
        for (int woeid : woeids) {
            if (woeid != curCityWoeid) {
                WeatherManager.loadWeather(getBaseContext(), woeid);
            }
        }
    }
}