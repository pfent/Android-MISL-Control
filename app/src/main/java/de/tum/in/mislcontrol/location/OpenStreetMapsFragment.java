package de.tum.in.mislcontrol.location;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;

import de.tum.in.mislcontrol.R;

/**
 * A fragment using OpenStreetMaps.
 */
public class OpenStreetMapsFragment extends Fragment implements IMapView {

    private MapView mMapView;

    private IMapController mMapController;

    /**
     * The initial zoom level of the map in range [0,19], where 0 is the lowest (whole world).
     */
    private int initialZoomLevel = 18;

    /**
     * The initial center of the map
     */
    private GeoPoint initialCenter = new GeoPoint(0.0, 0.0);

    /**
     * The last position, to know where to start a line from.
     */
    private GeoPoint lastPosition;

    public OpenStreetMapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param zoomLevel The zoom level.
     * @param centerLatitude The latitude value of the maps center.
     * @param centerLongitude The longitude value of the maps center
     * @return A new instance of fragment OpenStreetMapsFragment.
     */
    public static OpenStreetMapsFragment newInstance(int zoomLevel, double centerLatitude, double centerLongitude) {
        OpenStreetMapsFragment fragment = new OpenStreetMapsFragment();
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
            initialCenter = new GeoPoint(lat, lng);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_open_street_maps, container, false);

        if (rootView != null) {
            mMapView = (MapView) rootView.findViewById(R.id.mapView);
            //mMapView.setTileSource(TileSourceFactory.MAPNIK);
            mMapView.setBuiltInZoomControls(true);
            mMapView.setMultiTouchControls(true);
            mMapController = mMapView.getController();
            resetCamera();
        }

        return rootView;
    }

    @Override
    public void resetCamera() {
        mMapController.setZoom(initialZoomLevel);
        mMapController.setCenter(initialCenter);
    }

    // deprecation of PathOverlay is ignored, because new org.osmdroid.bonuspack.overlays.Polyline
    // implementation has a projection bug.
    @SuppressWarnings("deprecation")
    @Override
    public void addRouteLocation(double lat, double lng) {
        GeoPoint newPosition = new GeoPoint(lat, lng);

        if (lastPosition != null) {
            // add a line when this is not the first position
            PathOverlay line = new PathOverlay(Color.RED, 3.0f, new DefaultResourceProxyImpl(getActivity()));
            line.addPoint(lastPosition);
            line.addPoint(newPosition);
            line.setColor(Color.RED);
            mMapView.getOverlays().add(line);
        }

        // update center position to new location
        mMapController.setCenter(newPosition);

        // set the position as latest position
        lastPosition = newPosition;
    }

    @Override
    public void clearRoute() {
        mMapView.getOverlayManager().clear();
        lastPosition = null;
    }
}
