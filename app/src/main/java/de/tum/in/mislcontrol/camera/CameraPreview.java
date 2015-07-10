package de.tum.in.mislcontrol.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * A simple class that shows a camera preview.
 * This code is taken from the Android SDK API page:
 * URL: http://developer.android.com/guide/topics/media/camera.html
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String LOG_TAG = "CameraPreview";

    /**
     * The surface holder.
     */
    private final SurfaceHolder surfaceHolder;

    /**
     * The Android camera implemention (we use the old implementation to support Android API 14)
     */
    private final Camera camera;

    /**
     * Creates a camera preview instance.
     * @param context The context.
     * @param camera The Android device camera.
     */
    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.camera = camera;

        // install a SurfaceHolder callbacks so we get notified about its lifecycle events.
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on old Android versions
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * The Surface has been created, so we now tell the camera where to draw the preview.
     * @param holder The surface holder instance.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException | RuntimeException e) {
            Log.w(LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    /**
     * The surface got destroyed.
     * @param holder The surface holder instance.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we take care about releasing our camera in the Android activity
    }

    /**
     * The surface got changed.
     * @param holder The surface holder instance.
     * @param format The format.
     * @param width The width.
     * @param height The height.
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making any changes
        try {
            camera.stopPreview();
        } catch (Exception e){
            Log.w(LOG_TAG, "Error stopping camera preview: " + e.getMessage());
        }
        
        try {
            // start preview with new settings
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

        } catch (Exception e){
            Log.d(LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * Gets the drawing surface.
     * @return The drawing surface.
     */
    public Surface getSurface() {
        if (surfaceHolder == null)
            return null;
        return surfaceHolder.getSurface();
    }
}
