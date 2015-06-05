package de.tum.in.mislcontrol;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tum.in.mislcontrol.communication.IConnector;
import de.tum.in.mislcontrol.communication.TelemetryPacket;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment implements IConnector.OnTelemetryReceivedListener {


    public DataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data, container, false);
    }


    @Override
    public void onTelemetryReceived(TelemetryPacket packet) {

    }

    @Override
    public void onTelemetryTimedOut() {

    }
}
