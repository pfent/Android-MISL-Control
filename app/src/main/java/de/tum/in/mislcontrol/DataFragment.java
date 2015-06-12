package de.tum.in.mislcontrol;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {


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

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        final View v = getView();
        if (v == null) return;
        xEuler = (TextView) v.findViewById(R.id.xEulerTextView);
        yEuler = (TextView) v.findViewById(R.id.yEulerTextView);
        zEuler = (TextView) v.findViewById(R.id.zEulerTextView);
        xAccel = (TextView) v.findViewById(R.id.xAccelTextView);
        yAccel = (TextView) v.findViewById(R.id.yAccelTextView);
        zAccel = (TextView) v.findViewById(R.id.zAccelTextView);
        latitude = (TextView) v.findViewById(R.id.LatitudeTextView);
        longitude = (TextView) v.findViewById(R.id.LongitudeTextView);
    }

    public void setEuler(float roll, float pitch, float yaw) {
        xEuler.setText(String.format("%.04f",roll));
        yEuler.setText(String.format("%.04f", pitch));
        zEuler.setText(String.format("%.04f", yaw));
    }

    public void setAcceleration(float x, float y, float z) {
        xAccel.setText(String.format("%.04f", x));
        yAccel.setText(String.format("%.04f", y));
        zAccel.setText(String.format("%.04f", z));
    }

    public void setLocation(float latitude, float longitude) {
        this.latitude.setText(String.format("%.04f", latitude));
        this.longitude.setText(String.format("%.04f", longitude));
    }

}
