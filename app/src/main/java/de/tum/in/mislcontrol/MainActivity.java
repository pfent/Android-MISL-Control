package de.tum.in.mislcontrol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import de.tum.in.mislcontrol.communication.ASEPConnector;
import de.tum.in.mislcontrol.communication.IConnector;
import de.tum.in.mislcontrol.communication.data.TelemetryPacket;
import de.tum.in.mislcontrol.controls.IInputController;
import de.tum.in.mislcontrol.location.GoogleMapsFragment;
import de.tum.in.mislcontrol.location.IMapView;
import de.tum.in.mislcontrol.model3d.IModel3dView;

public class MainActivity extends AppCompatActivity implements IConnector.OnTelemetryReceivedListener, GoogleMapsFragment.OnLocationViewInteractionListener {

    private final IConnector connection = new ASEPConnector();
    //private final IConnector connection = new MockConnector();

    private IInputController controller;

    private DataFragment dataFragment;

    private IMapView mapView;

    private IModel3dView model3dView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            // add fragments
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

        connection.setOnTelemetryReceivedListener(this);
        controller = (IInputController)findViewById(R.id.joystick);
        connection.setInputController(controller);
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
}
