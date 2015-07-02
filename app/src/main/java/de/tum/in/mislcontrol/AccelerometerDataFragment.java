package de.tum.in.mislcontrol;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * The data fragment to display the acceleration data.
 */
public class AccelerometerDataFragment extends Fragment {

    private TextView textViewAccX;
    private TextView textViewAccY;
    private TextView textViewAccZ;

    /**
     * Creates an AccelerometerDataFragment instance.
     */
    public AccelerometerDataFragment() {
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
        textViewAccX = (TextView) v.findViewById(R.id.textViewAccX);
        textViewAccY = (TextView) v.findViewById(R.id.textViewAccY);
        textViewAccZ = (TextView) v.findViewById(R.id.textViewAccZ);
    }

    /**
     * Sets the acceleration value.
     * @param x The x acceleration value.
     * @param y The y acceleration value.
     * @param z The z acceleration value.
     */
    public void setAcceleration(float x, float y, float z) {
        textViewAccX.setText(String.format("%.2f g(s)", x));
        textViewAccY.setText(String.format("%.2f g(s)", y));
        textViewAccZ.setText(String.format("%.2f g(s)", z));
    }
}
