package de.tum.in.mislcontrol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import de.tum.in.mislcontrol.communication.ASEPConnector;
import de.tum.in.mislcontrol.communication.IConnector;
import de.tum.in.mislcontrol.communication.MockConnector;
import de.tum.in.mislcontrol.communication.data.TelemetryPacket;
import de.tum.in.mislcontrol.controls.IControlValue;

public class MainActivity extends AppCompatActivity implements IConnector.OnTelemetryReceivedListener {

    //private final IConnector connection = new ASEPConnector();
    private final IConnector connection = new MockConnector();

    private IControlValue controller;

    private DataFragment dataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            dataFragment = new DataFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, dataFragment)
                    .commit();
        }

        connection.setOnTelemetryReceivedListener(this);
        controller = (IControlValue)findViewById(R.id.joystick);
        connection.setIControlValue(controller);
    }

    @Override
    public void onResume() {
        super.onResume();
        connection.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        connection.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onTelemetryReceived(final TelemetryPacket packet) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // update fragment UI
                dataFragment.setEuler(packet.getXEuler(), packet.getYEuler(), packet.getZEuler());
                dataFragment.setAcceleration(packet.getXAccel(), packet.getYAccel(), packet.getZAccel());
                dataFragment.setLocation(packet.getLatitude(), packet.getLongitude());
            }
        });
    }

    @Override
    public void onTelemetryTimedOut() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        "Connection timed out.\n Is ASEP still in range?", Toast.LENGTH_LONG).show();
            }
        });
    }
}
