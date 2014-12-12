package ru.ifmo.md.lesson8.CitiesLoaderClasses;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.ifmo.md.lesson8.DataClasses.City;
import ru.ifmo.md.lesson8.DataClasses.WeatherManager;
import ru.ifmo.md.lesson8.R;

/**
 * Created by german on 05.12.14.
 */
public class CityAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private final Context mContext;
    private List<City> mResults;

    public CityAutoCompleteAdapter(Context context) {
        mContext = context;
        mResults = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public City getItem(int position) {
        return mResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View row = convertView;
        final TextView cityView;
        final TextView countryView;
        final ImageButton importantButton;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.city_item_dropdown, parent, false);
            cityView = (TextView) row.findViewById(android.R.id.text1);
            countryView = (TextView) row.findViewById(android.R.id.text2);
            importantButton = (ImageButton) row.findViewById(R.id.add_to_important_button);
            row.setTag(5 << 25, cityView);
            row.setTag(5 << 25 + 1, countryView);
            row.setTag(5 << 25 + 2, importantButton);
        } else {
            cityView = (TextView) row.getTag(5 << 25);
            countryView = (TextView) row.getTag(5 << 25 + 1);
            importantButton = (ImageButton) row.getTag(5 << 25 + 2);
        }
        final City curCity = getItem(position);
//        final int cityId = WeatherManager.getCityId(mContext.getContentResolver(), curCity);
//        final String importantly = WeatherManager.getImportantly(mContext.getContentResolver(), cityId);

        cityView.setText(curCity.getCityName());
        countryView.setText(curCity.getCountryName());
        WeatherManager.setImportantlyOnImage(mContext.getContentResolver(), curCity, importantButton);
//        if (importantly.equals(WeatherContentProvider.isImportant)) {
//            importantButton.setImageResource(WeatherContentProvider.importantDrawable);
//        } else {
//            importantButton.setImageResource(WeatherContentProvider.notImportantDrawable);
//        }

        importantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Item button clicked", " clicked " + getItem(position).toString());
                WeatherManager.changeImportantlyAndImage(mContext.getContentResolver(), curCity, importantButton);
            }
        });
        return row;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<City> cities = findCities(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = cities;
                    filterResults.count = cities.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mResults = (List<City>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private List<City> findCities(String pattern) {
        return CitiesLoader.getCities(mContext, pattern);
    }
}