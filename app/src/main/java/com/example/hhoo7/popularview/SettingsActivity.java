package com.example.hhoo7.popularview;


import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 调用装载有设置选项的菜单xml文件
        addPreferencesFromResource(R.xml.preferences);

        //绑定设置选项的key值
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_language_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_movieSort_key)));
    }

    /*
    * 自定义方法，设置监听器，查看设置中的选项是否有变更
    * */
    private void bindPreferenceSummaryToValue(Preference preference) {
        //设置监听器，查看设置中的选项是否有变更
        preference.setOnPreferenceChangeListener(this);

        //有选项变更时立即将preference文件中的value进行相应的变更
        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }

}
