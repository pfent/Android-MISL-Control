package de.tum.in.mislcontrol;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.util.List;

import de.psdev.licensesdialog.LicensesDialog;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    /**
     * Sets the default values of the shared preferences. This method must be called in any other
     * activity through which the user may enter your application for the first time.
     */
    public static void setDefaultValues() {
        PreferenceManager.setDefaultValues(ASEPApplication.getAppContext(),
                R.xml.fragmented_network_preferences, false);
        PreferenceManager.setDefaultValues(ASEPApplication.getAppContext(),
                R.xml.fragmented_control_preferences, false);
    }

    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    public static class NetworkPrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragmented_network_preferences);
        }
    }

    public static class ControlPrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragmented_control_preferences);
        }
    }

    public static class AboutPrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.fragmented_about_preferences);

            findPreference(getString(R.string.preferenceKey_linkToMisl))
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent link = new Intent(Intent.ACTION_VIEW);
                            link.setData(Uri.parse(getString(R.string.link_misl_esetwiki)));
                            startActivity(link);
                            return true;
                        }
                    });

            findPreference(getString(R.string.preferenceKey_linkToGithub))
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent link = new Intent(Intent.ACTION_VIEW);
                            link.setData(Uri.parse(getString(R.string.link_github_mislcontrol)));
                            startActivity(link);
                            return true;
                        }
                    });

            findPreference(getString(R.string.preferenceKey_openSourceLicenses))
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new LicensesDialog.Builder(getActivity())
                            .setNotices(R.raw.notices)
                            .build().show();
                    return true;
                }
            });
        }


    }

}
