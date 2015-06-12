package de.tum.in.mislcontrol;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import de.tum.in.mislcontrol.communication.IConnector;
import de.tum.in.mislcontrol.communication.data.TelemetryPacket;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment implements IConnector.OnTelemetryReceivedListener,
        SeekBar.OnSeekBarChangeListener {

    private final ASEPAdapter connection = new ASEPAdapter(this);
    private SeekBar straight;
    private SeekBar steering;
    private TextView xEuler;
    private TextView yEuler;
    private TextView zEuler;
    private TextView xAccel;
    private TextView yAccel;
    private TextView zAccel;
    private TextView latitude;
    private TextView longitude;

    private double xDirection = 0;
    private double yDirection = 0;

    public DataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_data, container, false);

        straight = (SeekBar) v.findViewById(R.id.straightCommandSeekBar);
        steering = (SeekBar) v.findViewById(R.id.steeringCommandSeekBar);
        xEuler = (TextView) v.findViewById(R.id.xEulerTextView);
        yEuler = (TextView) v.findViewById(R.id.yEulerTextView);
        zEuler = (TextView) v.findViewById(R.id.zEulerTextView);
        xAccel = (TextView) v.findViewById(R.id.xAccelTextView);
        yAccel = (TextView) v.findViewById(R.id.yAccelTextView);
        zAccel = (TextView) v.findViewById(R.id.zAccelTextView);
        latitude = (TextView) v.findViewById(R.id.LatitudeTextView);
        longitude = (TextView) v.findViewById(R.id.LongitudeTextView);

        straight.setOnSeekBarChangeListener(this);
        steering.setOnSeekBarChangeListener(this);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data, container, false);
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
    public void onDetach() {
        super.onDetach();
        connection.stop();
    }

    @Override
    public void onTelemetryReceived(final TelemetryPacket packet) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                xEuler.setText(Float.toString(packet.getXEuler()));
                yEuler.setText(Float.toString(packet.getYEuler()));
                zEuler.setText(Float.toString(packet.getZEuler()));
                xAccel.setText(Float.toString(packet.getXAccel()));
                yAccel.setText(Float.toString(packet.getYAccel()));
                zAccel.setText(Float.toString(packet.getZAccel()));
                latitude.setText(Float.toString(packet.getLatitude()));
                longitude.setText(Float.toString(packet.getLongitude()));
            }
        });
    }

    @Override
    public void onTelemetryTimedOut() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),
                        "Connection timed out.\n Is ASEP still in range?", Toast.LENGTH_LONG).show();
            }
        });
    }

    //TODO this does somehow not get called ATM?
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        final double seekBarPercentage = progress / seekBar.getMax(); // Range 0 ~ 1
        final double normalized = seekBarPercentage * 2 - 1; // Range -1 ~ 1

        if (seekBar == straight) {
            xDirection = normalized;
        } else if (seekBar == steering) {
            yDirection = normalized;
        }

        connection.drive(xDirection, yDirection);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}