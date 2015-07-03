package de.tum.in.mislcontrol.controls;

import android.view.MotionEvent;
import android.view.View;

import de.tum.in.mislcontrol.math.MathHelper;
import de.tum.in.mislcontrol.math.Vector2D;

/**
 * The controller implementation of the virtual joystick to handle touch events.
 */
public class JoystickController implements View.OnTouchListener {
    /**
     * The joystick control model.
     */
    private final ControlModel model;

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
     * Creates a joystick control controller instance.
     * @param model The model.
     */
    public JoystickController(ControlModel model) {
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
            // set relative value
            Vector2D relativeValue = touchPositionToRelativeValue(new Vector2D(event.getX(), event.getY()));
            model.setRelativeValue(relativeValue);
        } else {
            model.reset();
        }
    }

    /**
     * Translate the touch position to the relative control value.
     * @param touch The touch position relative to the top left of the view.
     * @return The translated relative control value.
     */
    private Vector2D touchPositionToRelativeValue(Vector2D touch) {
        double valueX = (touch.getX() - model.getBoundingBoxWidth() / 2) / model.getBackgroundWidth();
        double relativeX = MathHelper.between(valueX, -1, 1);

        double valueY = (touch.getY() - model.getBoundingBoxHeight() / 2) / model.getBackgroundHeight();
        double relativeY = MathHelper.between(valueY, -1, 1);

        Vector2D vector = new Vector2D(relativeX, -relativeY);
        if (vector.lengthSquared() > 1)
            return vector.normalize();
        else
            return vector;
    }
}
