package com.hahattpro.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by haha on 3/24/2015.
 */
public class ForecastFragment extends Fragment {
    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //Tell fragment that it have menu
        //there will not 3 dot, if your phone have menu button on hardware
        // in genymotion, press ctrl+M to show menu
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id ==R.id.action_refresh)
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //use string-array in resource xml
        //  ArrayList<String> arrayList = new ArrayList<String>();
        //   Collections.addAll(arrayList,getResources().getStringArray(R.array.forecast_example));

        //hardcode array into .java
        String tmp[] = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 40/30",
                "Web - Raining - 88/63",
                "Tue - Sunny - 40/30",
                "Thu - Foggy - 88/63",
                "Sat - Raining - 88/56",
                "Sun - Sunny - 23/63"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(tmp));

        //for debug
        // for (int i=0;i<arrayList.size();i++)
        //  Log.i("arrayList",arrayList.get(i));

        //create ArrayAdapter<String>
        ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(
                //Current context
                getActivity(),
                //id of List item layout
                R.layout.list_item_forecast,
                //id of textview to popular
                R.id.list_item_forecast_textview,
                //array which is forecast data
                weekForecast);


        //set reference to listView
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);

        //bind Array Adapter to
        listView.setAdapter(mForecastAdapter);
        new FetchWeatherTask().execute("still nothing here");
        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String,Void,String> {
        /////////////////
// These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.

        private String Cl_name = FetchWeatherTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            Log.i(Cl_name,"start");
            try
            {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
                Log.i(Cl_name,"start url");
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Log.i(Cl_name,"done url");
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.i(Cl_name,"inputStream null");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                Log.i(Cl_name,line);
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    Log.i(Cl_name,"buffer =0");
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.i(Cl_name,"Done");
            }

            catch(
                    IOException e
                    )

            {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            }

            finally
            {
                Log.i(Cl_name,"finally");
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
                Log.i(Cl_name,"reader not null");
                return null;
            }


        }


        //////////////////
    }

}

