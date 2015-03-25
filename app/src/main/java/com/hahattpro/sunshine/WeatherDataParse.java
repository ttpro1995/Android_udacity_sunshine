package com.hahattpro.sunshine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by haha on 3/25/2015.
 */
public class WeatherDataParse  {

    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {
        // TODO: add parsing code here

        JSONObject root = new JSONObject(weatherJsonStr);
        JSONArray listArray = root.getJSONArray("list");
        JSONObject day = listArray.getJSONObject(dayIndex);
       JSONObject temp = day.getJSONObject("temp");
        double max = temp.getDouble("max");

        return  max;
       // return -1;
    }

}
