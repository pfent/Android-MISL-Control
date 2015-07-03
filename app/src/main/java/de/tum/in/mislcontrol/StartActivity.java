package de.tum.in.mislcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import de.tum.in.mislcontrol.communication.ASEPConnector;
import de.tum.in.mislcontrol.communication.IConnector;

/**
 * The initial activity of the application. It shows a splash screen and checks for the connection
 * to the ASEP robot. It auto forwards to the main activity when a connection could be established
 * automatically. If not, it guides the user how to setup the connection.
 */
public class StartActivity extends FragmentActivity {

    /**
     * The key to identify the setup wizard dialog in the visual tree.
     */
    public static final String WIZARD_DIALOG_KEY = "wizardDialog";

    /**
     * The handler for delayed events.
     */
    private final Handler delayedActionHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IConnector connector = new ASEPConnector(this);

        setContentView(R.layout.activity_start);
        if (connector.checkConnection()) {
            // auto forward to main activity
            delayedActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }, 2000);
        } else {
            setContentView(R.layout.activity_start_wizard);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
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
}
