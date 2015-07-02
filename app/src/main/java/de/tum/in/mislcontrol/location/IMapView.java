package de.tum.in.mislcontrol.location;

/**
 * The interface for a map view to show the ASEP location.
 */
public interface IMapView {
    // the fragment initialization parameters
    String ARG_ZOOM_LEVEL = "zoomLevel";
    String ARG_CENTER_LAT = "centerLat";
    String ARG_CENTER_LNG = "centerLng";

    /**
     * Resets the maps camera center and zoom.
     */
    void resetCamera();

    /**
     * Adds a new position to the path. The camera will be centered to that new position.
     * @param lat The latitude value.
     * @param lng The longitude value.
     */
    void addRouteLocation(double lat, double lng);

    /**
     * Clears all positions of the route.
     */
    void clearRoute();
}
