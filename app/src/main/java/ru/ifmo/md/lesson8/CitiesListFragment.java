package ru.ifmo.md.lesson8;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import ru.ifmo.md.lesson8.DataClasses.WeatherContentProvider;
import ru.ifmo.md.lesson8.R;


public class CitiesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    SimpleCursorAdapter adapter;
    ListView citiesList;


    /**
     * This interface is created for WeatherFragment, because
     * it must to update information about current city
     */
    public interface OnItemSelectedListener {
        public void wasSelected(String city, String country);
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
                        WeatherContentProvider.COUNTRY_NAME
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
                city = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContentProvider.CITY_NAME));
                country = cursor.getString(cursor.getColumnIndexOrThrow(WeatherContentProvider.COUNTRY_NAME));
                myCallback.wasSelected(city, country);
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity().getApplicationContext(),
                WeatherContentProvider.CITIES_CONTENT,
                null, null, null, null);
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
