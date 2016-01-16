package com.arthur_guo.SafeToEat;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.os.Bundle;

import com.arthur_guo.SafeToEat.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    Preference weight;
    Preference error;
    Preference page;
    Preference distance;
    Preference customRanking;
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        addPreferencesFromResource(R.xml.preferences);
        weight = findPreference("crit_weight");
        error = findPreference("margin_error");
        page = findPreference("default_page");
        distance = findPreference("default_distance");
        customRanking = findPreference("custom_ranking");

        customRanking.setOnPreferenceChangeListener(this);
        weight.setOnPreferenceChangeListener(this);
        error.setOnPreferenceChangeListener(this);
        page.setOnPreferenceChangeListener(this);
        distance.setOnPreferenceChangeListener(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        onPreferenceChange(customRanking, prefs.getBoolean("custom_ranking", false));
        onPreferenceChange(weight, prefs.getString("crit_weight", "2"));
        onPreferenceChange(error,prefs.getString("margin_error", "1 non-critical infraction"));
        onPreferenceChange(page,prefs.getString("default_page", "Display All"));
        onPreferenceChange(distance,prefs.getString("default_distance", "500M"));


    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof ListPreference) {
            if (preference.getTitle().toString().equals("Critical Infraction Weighting")) {
                preference.setSummary("Each critical infraction is equal to " + (String) newValue + " non-critical infractions");
            } else if (preference.getTitle().toString().equals("Margin of Error")) {
                preference.setSummary("Anything below " + newValue + " will be considered as half its original weight");
            } else if (preference.getTitle().toString().equals("Default Page")) {
                preference.setSummary("Open the " + newValue + " page on app launch");
            } else if (preference.getTitle().toString().equals("Default Distance")) {
                preference.setSummary("Show faculties within " + newValue + " on app launch");
            }

        }

        if (preference instanceof SwitchPreference){
            if (!(Boolean) newValue) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("crit_weight", "2");
                editor.putString("margin_error", "1 non-critical infraction");
                editor.commit();
                onPreferenceChange(weight, prefs.getString("crit_weight", "ERROR"));
                onPreferenceChange(error, prefs.getString("margin_error", "ERROR"));
            }
        }

        return true;
    }
}
