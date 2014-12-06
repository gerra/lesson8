package ru.ifmo.md.lesson8.CitiesLoaderClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.ifmo.md.lesson8.DataClasses.City;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TextView cityView;
        TextView countryView;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.simple_dropdown_item_2line, parent, false);
            cityView = (TextView) row.findViewById(android.R.id.text1);
            countryView = (TextView) row.findViewById(android.R.id.text2);
            row.setTag(5 << 25, cityView);
            row.setTag(5 << 25 + 1, countryView);
        } else {
            cityView = (TextView) row.getTag(5 << 25);
            countryView = (TextView) row.getTag(5 << 25 + 1);
        }
        cityView.setText(getItem(position).getCityName());
        countryView.setText(getItem(position).getCountryName());
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
        return CitiesLoader.getCities(pattern);
    }
}