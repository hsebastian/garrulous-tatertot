package com.example.hsebastian.popularmoviesvol1;

import android.os.Bundle;
import android.preference.PreferenceFragment;



public class SettingsActivityFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
