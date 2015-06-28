package de.tum.in.mislcontrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import de.tum.in.mislcontrol.communication.ASEPConnector;
import de.tum.in.mislcontrol.communication.IConnector;
import de.tum.in.mislcontrol.communication.data.TelemetryPacket;
import de.tum.in.mislcontrol.controls.IInputController;
import de.tum.in.mislcontrol.controls.JoystickView;
import de.tum.in.mislcontrol.controls.SensorControlView;
import de.tum.in.mislcontrol.location.GoogleMapsFragment;
import de.tum.in.mislcontrol.location.IMapView;
import de.tum.in.mislcontrol.model3d.IModel3dView;

public class MainActivity extends AppCompatActivity implements IConnector.OnTelemetryReceivedListener, GoogleMapsFragment.OnLocationViewInteractionListener {

    /**
     * The connection to ASEP.
     */
    private final IConnector connection = new ASEPConnector();

    /**
     * The input controller to steer ASEP.
     */
    private IInputController inputController;

    /**
     * The data fragment to (temporarily) visualize the data.
     */
    private DataFragment dataFragment;

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
        setContentView(R.layout.activity_main);

        // add fragments
        if (savedInstanceState == null) {
            dataFragment = new DataFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.dataContainer, dataFragment)
                    .commit();

            // check if layout has a container for the map
            /*if (findViewById(R.id.mapContainer) != null) {
                GoogleMapsFragment mapFragment = GoogleMapsFragment.newInstance(19, 30.617326, -96.341768); // Texas A&M
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.mapContainer, mapFragment)
                        .commit();
                mapView = mapFragment;
            }*/

            if (findViewById(R.id.model3dFragment) != null) {
                Model3DFragment model3DFragment = new Model3DFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.model3dFragment, model3DFragment)
                        .commit();
                model3dView = model3DFragment;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        connection.start();

        // add dome dummy data
        /*mapView.addRouteLocation(30.617178, -96.341969);
        mapView.addRouteLocation(30.616883, -96.342448);
        mapView.addRouteLocation(30.617204, -96.342847);
        mapView.addRouteLocation(30.617436, -96.343084);
        mapView.addRouteLocation(30.617070, -96.343109);*/

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
                dataFragment.setEuler(packet.getXEuler(), packet.getYEuler(), packet.getZEuler());
                dataFragment.setAcceleration(packet.getXAccel(), packet.getYAccel(), packet.getZAccel());
                dataFragment.setLocation(packet.getLatitude(), packet.getLongitude());

                //model3dView.setRotation(packet.getXEuler(), packet.getZEuler(), packet.getYEuler());
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

    @Override
    public void onMaximize() {

    }

    @Override
    public void onMinimize() {

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
        }

        ViewGroup controlContainer = (ViewGroup)findViewById(R.id.controlContainer);
        if (newControlType == null || newControlType.equals(getString(R.string.setting_controlType_joystick))) {
            JoystickView controlView = new JoystickView(this);
            controlContainer.addView(controlView);
            inputController = controlView;
        } else {
            SensorControlView controlView = new SensorControlView(this);
            controlContainer.addView(controlView);
            inputController = controlView;
        }
    }

    /**
     * removes the control view in the visual tree.
     */
    private void removeControlView() {
        ViewGroup controlContainer = (ViewGroup)findViewById(R.id.controlContainer);
        if (controlContainer.getChildCount() > 0) {
            controlContainer.removeAllViews();
        }
        inputController = null;
    }
}
