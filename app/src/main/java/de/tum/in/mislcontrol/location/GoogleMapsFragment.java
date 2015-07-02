package de.tum.in.mislcontrol.location;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.mislcontrol.R;

/**
 * The Google Maps fragment to display the location and track of the ASEP robot.
 */
public class GoogleMapsFragment extends Fragment implements IMapView, OnMapReadyCallback {
    /**
     * The map view.
     */
    MapView mapView;

    /**
     * The google map.
     */
    GoogleMap googleMap;

    /**
     * The initial zoom level of the map in range [0,19], where 0 is the lowest (whole world).
     */
    private int initialZoomLevel = 19;

    /**
     * The initial center of the map
     */
    private LatLng initialCenter = new LatLng(0, 0);

    /**
     * A flag that indicates whether the map is ready to use.
     */
    private boolean isMapReady = false;

    /**
     * A cache for locations that are added before the map was ready.
     */
    private List<LatLng> pathCache = new ArrayList<>();

    /**
     * The path of the robot.
     */
    private PolylineOptions path;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initialZoomLevel = getArguments().getInt(ARG_ZOOM_LEVEL);
            double lat = getArguments().getDouble(ARG_CENTER_LAT);
            double lng = getArguments().getDouble(ARG_CENTER_LNG);
            initialCenter = new LatLng(lat, lng);
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

    @Override
    public void onMapReady(GoogleMap map) {
        isMapReady = true;
        this.googleMap = map;
        resetCamera();
        clearRoute();

        // add path positions from cache
        if (!pathCache.isEmpty()) {
            LatLng lastLocation = null;
            for (LatLng loc : pathCache) {
                lastLocation = loc;
                path.add(loc);
            }
            updateRoute();
            pathCache.clear();
            if (lastLocation != null) {
                // move camera to last position
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
            }
        }
    }

    @Override
    public void resetCamera()
    {
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(initialZoomLevel));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(initialCenter));
    }

    @Override
    public void addRouteLocation(double lat, double lng) {
        // ignore UNKNOWN locations
        if (lat == 0 && lng == 0)
            return;

        LatLng newPosition = new LatLng(lat, lng);

        if (isMapReady) {
            path.add(newPosition);
            updateRoute();
            // set map center to new position
            if (googleMap != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(newPosition));
        } else {
            pathCache.add(newPosition);
        }
    }

    @Override
    public void clearRoute() {
        googleMap.clear();

        // add empty path
        path = new PolylineOptions();
        path.width(2.0f);
        path.visible(true);
        path.color(Color.RED);
        googleMap.addPolyline(path);
    }

    /**
     * Forces the map to update the route
     */
    private void updateRoute() {
        googleMap.clear();
        googleMap.addPolyline(path);
    }
}
