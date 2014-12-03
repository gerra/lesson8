package ru.ifmo.md.lesson8.DataClasses;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.ifmo.md.lesson8.R;

/**
 * Created by german on 30.11.14.
 */
public class ForecastAdapter extends CursorAdapter {
    private LayoutInflater inflater;

    public ForecastAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View forecastView = inflater.inflate(R.layout.forecast_item, null);
        return forecastView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null && view != null) {
            // f = forecast
            TextView fDateView = (TextView)view.findViewById(R.id.forecast_day);
            ImageView fCloudyView = (ImageView)view.findViewById(R.id.forecast_cloudy);
            TextView fTempLowView = (TextView)view.findViewById(R.id.forecast_temp_low);
            TextView fTempHighView = (TextView)view.findViewById(R.id.forecast_temp_high);

            String fDate = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContentProvider.FORECAST_DATE));
            String fTempLow = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContentProvider.FORECAST_TEMP_LOW));
            String fTempHigh = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContentProvider.FORECAST_TEMP_HIGH));
            int fCode = cursor.getInt(cursor.getColumnIndexOrThrow(WeatherContentProvider.FORECAST_CODE));

            fDateView.setText(fDate);
            fCloudyView.setImageResource(WeatherManager.getCloudyId(fCode));
            fTempLowView.setText(fTempLow);
            fTempHighView.setText(fTempHigh);
        }
    }
}
