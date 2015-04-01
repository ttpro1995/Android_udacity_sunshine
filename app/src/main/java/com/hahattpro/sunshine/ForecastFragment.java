package com.hahattpro.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by haha on 3/24/2015.
 */
public class ForecastFragment extends Fragment {
    public ForecastFragment() {
    }
    public ListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //Tell fragment that it have menu
        //there will not 3 dot, if your phone have menu button on hardware
        // in genymotion, press ctrl+M to show menu


    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //contail number code or name of city
        String Location  = pref.getString(getString(R.string.set_location_key),getString(R.string.set_location_default_value));

        //default 94043
        new FetchWeatherTask().execute(Location);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        if (id==R.id.action_settings)
        {
            Intent intent = new Intent(getActivity(),SettingsActivity.class);
            startActivity(intent);
        }
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
         listView = (ListView) rootView.findViewById(R.id.listview_forecast);

        //bind Array Adapter to
        listView.setAdapter(mForecastAdapter);
        //  new FetchWeatherTask().execute("still nothing here");
        return rootView;
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        String format ="json";
        String unit ="metric";
        int Days =7;

        /* The date/time conversion code is going to be moved outside the asynctask later,
  * so for convenience we're breaking it out into its own method now.
  */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);



            String highLowStr = roundedHigh + "/" + roundedLow;


            return highLowStr;
        }


        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;


                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
                String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                String QUERY =  "q";
                String FORMAT = "mode";
                String UNIT = "units";//unit co "s" nha
                String DAY ="cnt";
                String APPID = "APPID";

                //select metric or imperial unit
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                unit = pref.getString(getString(R.string.unit_key),getString(R.string.unit_default));

                Uri uribuilder = Uri.parse(FORECAST_BASE_URL).buildUpon()
                                .appendQueryParameter(QUERY,params[0])
                        .appendQueryParameter(FORMAT,format)
                        .appendQueryParameter(UNIT,unit)
                        .appendQueryParameter(DAY,Integer.toString(Days))
                        .appendQueryParameter(APPID,getResources().getString(R.string.API_key))
                        .build();

                URL url = new URL (uribuilder.toString());

                Log.e("URL",url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
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

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

                Log.i(LOG_TAG,forecastJsonStr);//log json string

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                String forecast[] = getWeatherDataFromJson(forecastJsonStr, 7);
                return forecast;
            }
            catch (JSONException e)
            {
                Log.i(LOG_TAG,"JSON exception");
            }


            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);


            ArrayList<String> weekForecast;
            final ArrayAdapter<String> arrayAdapter;

            try {
                weekForecast = new ArrayList<String>(Arrays.asList(strings));
                arrayAdapter = new ArrayAdapter<String>( //Current context
                        getActivity(),
                        //id of List item layout
                        R.layout.list_item_forecast,
                        //id of textview to popular
                        R.id.list_item_forecast_textview,
                        //array which is forecast data
                        weekForecast);


                listView.setAdapter(arrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.i(LOG_TAG + " on Item Click", "click " + position);
                        //remember to press refress so that FetchWeatherTask is execute
                        String forecast_str = parent.getItemAtPosition(position).toString();

                        Toast.makeText(
                                getActivity(),//content
                                forecast_str,//string for toast to display
                                Toast.LENGTH_SHORT)//length of toast, short or long
                                .show();//show() method must be call to show toast
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra(Intent.EXTRA_TEXT, forecast_str);
                        startActivity(intent);
                    }
                });
            }
            catch (NullPointerException e)
            {
                Log.e(LOG_TAG,e.getMessage());
            }
        }
    } // end of Fetch
}// end of ForecastFragment

