package de.tum.in.mislcontrol.controls;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * The controller implementation of the sensor controller to handle accelerometer and touch events.
 */
public class SensorControlController implements SensorEventListener, View.OnTouchListener  {
    /**
     * The joystick model.
     */
    private final SensorControlModel sensorControlModel;

    /**
     * The motion sensor manager.
     */
    private final SensorManager sensorManager;

    /**
     * The accelerometer sensor.
     */
    private final Sensor accelerometerSensor;

    /**
     * Indicates whether the users finger is currently down and dragging around the stick.
     */
    private boolean isDragging;

    /**
     * The stored event, which is used when an update is performed in a context where no motion
     * position can be retrieved.
     */
    private MotionEvent lastEvent;

    /**
     * Creates a sensor control controller instance.
     * @param sensorControlModel The model.
     */
    public SensorControlController(Context context, SensorControlModel sensorControlModel) {
        this.sensorControlModel = sensorControlModel;

        // sensor registration
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];



            Log.d(getClass().getName(), String.format("[%f, %f, %f]", x, y, z));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // NOP
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        update(event);
        return true;
    }

    /**
     * Updates the joystick.
     * @param event The motion event.
     */
    public void update(MotionEvent event) {
        // check if the last event has to be reused
        if (event == null && lastEvent != null) {
            event = lastEvent;
        } else if (event != null) {
            lastEvent = event;
        } else {
            // when there is no event, just perform a reset
            sensorControlModel.reset();
            return;
        }

        //drag drop
        if (event.getAction() == MotionEvent.ACTION_DOWN ) {
            // start dragging when the touch was inside of our surface
            if ((int)event.getX() >= 0 && (int)event.getX() <= sensorControlModel.getBoundingBoxWidth() &&
                    (int)event.getY() >= 0 && (int)event.getY() <= sensorControlModel.getBoundingBoxHeight())
                isDragging = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isDragging = false;
        }

        if (isDragging) {
            // set touch positions
            sensorControlModel.setTouchPosition(new Point(0, (int)event.getY()));
        } else {
            sensorControlModel.reset();
        }
    }
}
