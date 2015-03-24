package com.hahattpro.sunshine;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            //use string-array in resource xml
          //  ArrayList<String> arrayList = new ArrayList<String>();
         //   Collections.addAll(arrayList,getResources().getStringArray(R.array.forecast_example));

            //hardcode array into .java
            String tmp[] ={
                    "Today - Sunny - 88/63",
            "Tomorrow - Foggy - 40/30",
            "Web - Raining - 88/63</item>",
            "Tue - Sunny - 40/30</item>",
            "Thu - Foggy - 88/63</item>",
            "Sat - Raining - 88/56</item>",
            "Sun - Sunny - 23/63</item>"
            };

            List<String> arrayList = new ArrayList<String>(Arrays.asList(tmp));

        for (int i=0;i<arrayList.size();i++)
            Log.i("arrayList",arrayList.get(i));

            return rootView;
        }
    }
}
