package de.tum.in.mislcontrol.controls;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;

import de.tum.in.mislcontrol.math.MathHelper;
import de.tum.in.mislcontrol.math.Vector2D;

/**
 * The controller implementation of the sensor controller to handle accelerometer and touch events.
 */
public class SensorControlController implements SensorEventListener, View.OnTouchListener  {
    /**
     * The absolute steering threshold.
     */
    public static final double STEERING_MIN_THRESHOLD = 2.0;

    /**
     * The steering limit.
     */
    public static final double STEERING_MAX = 8.0;

    /**
     * The joystick model.
     */
    private final ControlModel model;

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
     * @param model The model.
     */
    public SensorControlController(Context context, ControlModel model) {
        this.model = model;

        // sensor registration
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0]; // negative: right; positive: left

            // calculate steering value
            double steeringValue = -x;
            boolean isLeftSteering = (steeringValue < 0);
            double steeringValueAbs = Math.abs(steeringValue);

            // check if threshold is reached
            if (steeringValueAbs > STEERING_MIN_THRESHOLD) {
                // adjust steering value to bounds
                steeringValueAbs = MathHelper.between(steeringValueAbs, STEERING_MIN_THRESHOLD, STEERING_MAX);
                // transform to relative value in range [-1,1]
                steeringValueAbs = (steeringValueAbs - STEERING_MIN_THRESHOLD) / (STEERING_MAX - STEERING_MIN_THRESHOLD);
            } else {
                steeringValueAbs = 0;
            }

            double newX = (isLeftSteering) ? -steeringValueAbs : steeringValueAbs;
            Vector2D oldValue = model.getRelativeValue();
            model.setRelativeValue(new Vector2D(newX, oldValue.getY()));
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
            model.reset();
            return;
        }

        //drag drop
        if (event.getAction() == MotionEvent.ACTION_DOWN ) {
            // start dragging when the touch was inside of our surface
            if ((int)event.getX() >= 0 && (int)event.getX() <= model.getBoundingBoxWidth() &&
                    (int)event.getY() >= 0 && (int)event.getY() <= model.getBoundingBoxHeight())
                isDragging = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isDragging = false;
        }

        if (isDragging) {
            // set relative value
            double relativeYOnScreen = touchPositionToRelativeValueY(event.getY());
            model.setRelativeValue(new Vector2D(0, -relativeYOnScreen));
        } else {
            model.reset();
        }
    }

    /**
     * Translate the y value of the touch position to the relative control value.
     * @param touchY The y value of the touch position relative to the top left of the view.
     * @return The translated relative control value.
     */
    private double touchPositionToRelativeValueY(double touchY) {
        double value = (touchY - model.getBoundingBoxHeight() / 2) / model.getBackgroundHeight();
        return MathHelper.between(value, -1, 1);
    }
}
