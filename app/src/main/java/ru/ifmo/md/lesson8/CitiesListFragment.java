package ru.ifmo.md.lesson8;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import ru.ifmo.md.lesson8.CitiesLoaderClasses.CityAutoCompleteAdapter;
import ru.ifmo.md.lesson8.CitiesLoaderClasses.DelayAutoCompleteTextView;
import ru.ifmo.md.lesson8.DataClasses.City;
import ru.ifmo.md.lesson8.DataClasses.WeatherContentProvider;
import ru.ifmo.md.lesson8.DataClasses.WeatherManager;


public class CitiesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    SimpleCursorAdapter adapter;
    ListView citiesList;

    /**
     * This interface is created for WeatherFragment, because
     * it must to update information about current city
     */
    public interface OnItemSelectedListener {
        public void wasSelected(City newCity);
    }

    OnItemSelectedListener myCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            myCallback = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cities_list, container, false);
        final DelayAutoCompleteTextView editCity = (DelayAutoCompleteTextView) view.findViewById(R.id.input_city);
        CityAutoCompleteAdapter cityAdapter = new CityAutoCompleteAdapter(getActivity().getApplicationContext());
        editCity.setAdapter(cityAdapter);
        editCity.setThreshold(3);
        editCity.setLoadingIndicator((ProgressBar) view.findViewById(R.id.city_progress_bar));
        editCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Dropdown list item selected", "clicked " + position);
                City curCity = (City) parent.getItemAtPosition(position);
                myCallback.wasSelected(curCity);
                editCity.setText("");
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        citiesList = (ListView) getView().findViewById(R.id.city_list);
        adapter = new SimpleCursorAdapter(
                getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_2,
                null,
                new String[] {
                        WeatherContentProvider.CITY_NAME,
                        WeatherContentProvider.COUNTRY_NAME,
                        WeatherContentProvider.WOEID
                },
                new int[] {
                        android.R.id.text1,
                        android.R.id.text2
                }, 0);
        citiesList.setAdapter(adapter);

        citiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                String city, country;
                int woeid;
                city = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContentProvider.CITY_NAME));
                country = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContentProvider.COUNTRY_NAME));
                woeid = cursor.getInt(cursor.getColumnIndexOrThrow(WeatherContentProvider.WOEID));
                myCallback.wasSelected(new City(city, country, woeid));
            }
        });
        citiesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                String cityName, countryName;
                int woeid;
                cityName = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContentProvider.CITY_NAME));
                countryName = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContentProvider.COUNTRY_NAME));
                woeid = cursor.getInt(cursor.getColumnIndexOrThrow(WeatherContentProvider.WOEID));
                City city = new City(cityName, countryName, woeid);
                WeatherManager.setImportantly(getActivity().getContentResolver(), city, WeatherContentProvider.isNotImportant);
                return true;
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity().getApplicationContext(),
                WeatherContentProvider.CITIES_CONTENT,
                null,
                WeatherContentProvider.IS_IMPORTANT + " = ?",
                new String[] {
                        WeatherContentProvider.isImportant
                },
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
