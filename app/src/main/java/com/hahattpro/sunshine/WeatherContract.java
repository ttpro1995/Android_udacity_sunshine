package com.hahattpro.sunshine;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import java.net.URI;

/**
 * Created by haha on 4/3/2015.
 */
public class WeatherContract {


    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final String CONTENT_AUTHORITY = "com.hahattpro.sunshine.app";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    /*
        Inner class that defines the table contents of the location table
        Students: This is where you will add the strings.  (Similar to what has been
        done for WeatherEntry)
     */
    public static final class LocationEntry implements BaseColumns {

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String TABLE_NAME = "location";

        //query for our API request
        public static final String COLUMN_LOCATION_SETTING ="Location_Setting";

        //city name
        public static final String COLUMN_CITY_NAME = "City_Name";

        //coord
        public static final String COLUMN_COORD_LAT="Coord_lat";
        public static final String COLUMN_COORD_LONG="Coord_long";
    }

    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static Uri buildWeatherUri(long id)
        {
            Uri uri=null;
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildWeatherLocationWithDate(String LOCATION_QUERY,long TEST_DATE)
        {
            Uri uri = null;
            uri = CONTENT_URI.buildUpon().appendPath(LOCATION_QUERY).appendPath(Long.toString(normalizeDate(TEST_DATE))).build();
            return uri;
        }

        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";

        public static Uri buildWeatherLocation(String locationSetting)
        {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }


        public static Uri buildWeatherLocationWithStartDate(String a, long b)
        {
            return null;
        }

        public static String getLocationSettingFromUri(Uri a)
        {
            return null;
        }

        public static long getStartDateFromUri(Uri a)
        {
            return 0;
        }

        public static long getDateFromUri(Uri a)
        {
            return 0;
        }
    }
}
