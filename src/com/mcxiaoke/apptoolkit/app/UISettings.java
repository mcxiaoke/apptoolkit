package com.mcxiaoke.apptoolkit.app;

import android.os.Bundle;
import android.preference.Preference;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.mcxiaoke.apptoolkit.AppContext;
import com.mcxiaoke.apptoolkit.R;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit.app
 * User: mcxiaoke
 * Date: 13-6-22
 * Time: 下午6:02
 */
public class UISettings extends SherlockPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.settings);

        StringBuilder builder = new StringBuilder();
        builder.append("v").append(AppContext.getVersionName()).append("  Build").append(AppContext.getVersionCode());
        String versionInfo = String.format(getString(R.string.pref_version_summary, builder.toString()));
        Preference version = findPreference(getString(R.string.pref_version_key));
        version.setSummary(versionInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
