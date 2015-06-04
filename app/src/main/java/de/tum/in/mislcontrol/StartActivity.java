package de.tum.in.mislcontrol;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import de.tum.in.mislcontrol.R;
import de.tum.in.mislcontrol.communication.ASEPConnector;
import de.tum.in.mislcontrol.communication.TelemetryPacket;

/**
 * The initial activity of the application. It shows a splash screen and checks for the connection
 * to the ASEP robot. It auto forwards to the main activity when a connection could be established
 * automatically. If not, it guides the user how to setup the connection.
 */
public class StartActivity extends AppCompatActivity implements ASEPConnector.TelemetryReceivedListener {

    /**
     * The handler for delayed events.
     */
    private final Handler delayedActionHandler = new Handler();

    /**
     * The ASEP connector.
     */
    private ASEPConnector asepConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    protected void onResume() {
        super.onResume();

        asepConnector = new ASEPConnector(this);
        asepConnector.start();

        delayedActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (asepConnector.checkConnection()) {
                    // auto forward to main activity
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                } else {
                    // diplay a wifi-setup guide popup
                    // TODO: show wifi-setup guide popup
                }
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();

        asepConnector.close();
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

    @Override
    public void onTelemetryReceived(TelemetryPacket packet) {
        // TODO make this callback optional?
    }

    @Override
    public void onTelemetryTimedOut() {
        // TODO make this callback optional?
    }
}
