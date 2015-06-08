package de.tum.in.mislcontrol.location;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import de.tum.in.mislcontrol.R;

/**
 * The Google Maps fragment to display the location and track of the ASEP robot.
 */
public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback {
    // the fragment initialization parameters
    public static final String ARG_ZOOM_LEVEL = "zoomLevel";
    public static final String ARG_CENTER_LAT = "centerLat";
    public static final String ARG_CENTER_LNG = "centerLng";

    /**
     * The map view.
     */
    MapView mapView;

    /**
     * The initial zoom level of the map in range [0,19], where 0 is the lowest (whole world).
     */
    private int zoomLevel;

    /**
     * The initial latitude value of the maps center.
     */
    private double centerLatitude;

    /**
     * The initial longitude value of the maps center.
     */
    private double centerLongitude;

    /**
     * The interaction listener of the location view to interact with the activity.
     */
    private OnLocationViewInteractionListener interactionListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param zoomLevel The zoom level.
     * @param centerLatitude The latitude value of the maps center.
     * @param centerLongitude The longitude value of the maps center
     * @return A new instance of fragment GoogleMapsFragment.
     */
    public static GoogleMapsFragment newInstance(int zoomLevel, double centerLatitude, double centerLongitude) {
        GoogleMapsFragment fragment = new GoogleMapsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ZOOM_LEVEL, zoomLevel);
        args.putDouble(ARG_CENTER_LAT, centerLatitude);
        args.putDouble(ARG_CENTER_LNG, centerLongitude);
        fragment.setArguments(args);
        return fragment;
    }

    public GoogleMapsFragment() {
        // required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            zoomLevel = getArguments().getInt(ARG_ZOOM_LEVEL);
            centerLatitude = getArguments().getDouble(ARG_CENTER_LAT);
            centerLongitude = getArguments().getDouble(ARG_CENTER_LNG);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_maps, container, false);

        // get a reference to the map view
        mapView = (MapView)view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    private void onMaximizeButtonPressed() {
        if (interactionListener != null) {
            interactionListener.onMaximize();
        }
    }

    private void onMinimizeButtonPressed() {
        if (interactionListener != null) {
            interactionListener.onMinimize();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            interactionListener = (OnLocationViewInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // TODO: actions after the map has been loaded goes here...
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnLocationViewInteractionListener {
        void onMaximize();
        void onMinimize();
    }
}
