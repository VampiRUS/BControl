
package ru.vampirus.bcontrol;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Preferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference ssidPref = (Preference) findPreference("ssid_preference");
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        ssidPref.setSummary(prefs.getString("ssid_preference", ""));
        ssidPref.setOnPreferenceChangeListener(
                new OnPreferenceChangeListener() {
                    /**
                     * update summary on ssid preference change
                     */
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        preference.setSummary((String) newValue);
                        return true;
                    }

                });

    }
}
