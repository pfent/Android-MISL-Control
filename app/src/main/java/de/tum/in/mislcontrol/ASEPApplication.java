package de.tum.in.mislcontrol;

import android.app.Application;
import android.content.Context;

/**
 * The android application class of the app.
 */
public class ASEPApplication extends Application {
    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        ASEPApplication.context = getApplicationContext();

        // set up the default settings
        SettingsActivity.setDefaultValues();
    }

    /**
     * Gets the application context in a static context.
     * @return The application context.
     */
    public static Context getAppContext() {
        return ASEPApplication.context;
    }
}
