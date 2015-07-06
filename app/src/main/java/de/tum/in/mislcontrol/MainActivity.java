package de.tum.in.mislcontrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import de.tum.in.mislcontrol.communication.ASEPConnector;
import de.tum.in.mislcontrol.communication.IConnector;
import de.tum.in.mislcontrol.communication.data.TelemetryPacket;
import de.tum.in.mislcontrol.controls.IInputController;
import de.tum.in.mislcontrol.controls.JoystickView;
import de.tum.in.mislcontrol.controls.SensorControlView;
import de.tum.in.mislcontrol.location.IMapView;
import de.tum.in.mislcontrol.location.OpenStreetMapsFragment;
import de.tum.in.mislcontrol.model3d.IModel3dView;

public class MainActivity extends AppCompatActivity implements IConnector.OnTelemetryReceivedListener {

    /**
     * The connection to ASEP.
     */
    private IConnector connection;

    /**
     * The input controller to steer ASEP.
     */
    private IInputController inputController;

    /**
     * The data fragment to (temporarily) visualize the data.
     */
    private AccelerometerDataFragment dataFragment;

    /**
     * The map view.
     */
    private IMapView mapView;

    /**
     * The view for the 3D model.
     */
    private IModel3dView model3dView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connection = new ASEPConnector(this);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume() {
        super.onResume();

        View dataContainer = findViewById(R.id.accelerometerDataContainer);
        if (dataContainer != null && dataFragment == null) {
            dataFragment = new AccelerometerDataFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.accelerometerDataContainer, dataFragment)
                    .commit();
        }

        // check if layout has a container for the map
        if (findViewById(R.id.mapContainer) != null && mapView == null) {
            OpenStreetMapsFragment mapFragment = OpenStreetMapsFragment.newInstance(19, 30.617326, -96.341768); // Texas A&M
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mapContainer, mapFragment)
                    .commit();
            mapView = mapFragment;
        }

        if (findViewById(R.id.model3dContainer) != null && model3dView == null) {
            Model3DFragment model3DFragment = new Model3DFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.model3dContainer, model3DFragment)
                    .commit();
            model3dView = model3DFragment;
        }

        connection.start();

        addOrReplaceControlView();

        connection.setOnTelemetryReceivedListener(this);
        connection.setInputController(inputController);
    }

    @Override
    public void onPause() {
        super.onPause();
        connection.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connection.close();
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTelemetryReceived(final TelemetryPacket packet) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // update fragment UI
                if (dataFragment != null) {
                    dataFragment.setAcceleration(packet.getXAccel(), packet.getYAccel(), packet.getZAccel());
                }
                if (mapView != null && (packet.getLatitude() != 0 || packet.getLongitude() != 0)) {
                    mapView.addRouteLocation(packet.getLatitude(), packet.getLongitude());
                }

                if (model3dView != null) {
                    model3dView.setRotation(packet.getXEuler(), packet.getZEuler(), packet.getYEuler());
                }

                TextView textViewX = (TextView)findViewById(R.id.textViewEulerX);
                TextView textViewY = (TextView)findViewById(R.id.textViewEulerY);
                TextView textViewZ = (TextView)findViewById(R.id.textViewEulerZ);

                textViewX.setText(String.format("%.5f", packet.getXEuler()));
                textViewY.setText(String.format("%.5f", packet.getYEuler()));
                textViewZ.setText(String.format("%.5f", packet.getZEuler()));
            }
        });
    }

    @Override
    public void onTelemetryTimedOut() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.connection_timeout), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Add or replaces the control view according the the shared preferences.
     */
    private void addOrReplaceControlView() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String newControlType = prefs.getString(getString(R.string.setting_controlType_key), getString(R.string.setting_controlType_joystick));

        // check if current control view has changed
        if (inputController != null && !inputController.getType().equals(newControlType)) {
            removeControlView();
        } else if (inputController != null) {
            return;
        }

        ViewGroup controlContainer = (ViewGroup) findViewById(R.id.controlContainer);
        if (newControlType == null || newControlType.equals(getString(R.string.setting_controlType_joystick))) {
            releaseOrientation();
            JoystickView controlView = new JoystickView(this);
            controlContainer.addView(controlView);
            inputController = controlView;
        } else {
            lockOrientation();
            SensorControlView controlView = new SensorControlView(this);
            controlContainer.addView(controlView);
            inputController = controlView;
        }
    }

    /**
     * removes the control view in the visual tree.
     */
    private void removeControlView() {
        ViewGroup controlContainer = (ViewGroup) findViewById(R.id.controlContainer);
        if (controlContainer.getChildCount() > 0) {
            controlContainer.removeAllViews();
        }
        inputController = null;
    }

    private void lockOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void releaseOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
