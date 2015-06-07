package de.tum.in.mislcontrol.controls;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

/**
 * The controller implementation of the virtual joystick to handle touch events.
 */
public class JoystickController implements View.OnTouchListener {
    /**
     * The joystick model.
     */
    private final JoystickModel model;

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
     * Creates a joystick controller instance.
     * @param model The model.
     */
    public JoystickController(JoystickModel model) {
        this.model = model;
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
            // set touch positions
            model.setTouchPosition(new Point((int) event.getX(), (int) event.getY()));
        } else {
            model.reset();
        }
    }
}
