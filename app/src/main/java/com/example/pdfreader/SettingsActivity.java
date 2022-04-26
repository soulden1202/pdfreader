package com.example.pdfreader;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;


public class SettingsActivity extends AppCompatActivity {

    private String theme = "";
    private SharedPreferences.OnSharedPreferenceChangeListener listener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Bundle extras = getIntent().getExtras();
        theme = extras.getString("theme");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        View ac = findViewById(R.id.acsetting);



        if(theme.equals("1"))
        {
            ac.setBackgroundColor(getResources().getColor(R.color.white));


        }
        else if(theme.equals("2")){
            ac.setBackgroundColor(getResources().getColor(R.color.black));

        }

        listener = (prefs1, key) -> {
            if (key.equals("themePref")) {
                String theme = prefs1.getString("themePref", "");
                Log.e("Theme", theme);
                if( theme.equals("1"))
                {
                    ac.setBackgroundColor(getResources().getColor(R.color.white));



                }
                else if( theme.equals("2"))
                {
                    ac.setBackgroundColor(getResources().getColor(R.color.black));



                }


            }
        };

        prefs.registerOnSharedPreferenceChangeListener(listener);

    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);

        }
    }
}
