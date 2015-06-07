package de.tum.in.mislcontrol.controls;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import de.tum.in.mislcontrol.math.Vector2D;

/**
 * The model class of a control element.
 */
public class ControlModel {
    /**
     * The bounding box width of the view.
     */
    private int boundingBoxWidth;

    /**
     * The bounding box height of the view.
     */
    private int boundingBoxHeight;

    /**
     * The relative control value in range x [-1,1] and y [-1,1], where the origin is in the center.
     */
    private volatile Vector2D relativeValue = new Vector2D();

    /**
     * The bitmap image of the control stick.
     */
    private final Bitmap stickBitmap;

    /**
     * The bitmap background image of the draggable area.
     */
    private final Bitmap backgroundBitmap;

    /**
     * The joystick model.
     * @param res The android resources.
     * @param stickResource The resource code for the stick image.
     * @param backgroundResource The resource code for the background image.
     */
    public ControlModel(Resources res, int stickResource, int backgroundResource) {
        // load image resources
        stickBitmap = BitmapFactory.decodeResource(res, stickResource);
        backgroundBitmap = BitmapFactory.decodeResource(res, backgroundResource);

        reset();
    }

    /**
     * Updates the bounding box.
     * @param width The width.
     * @param height The height.
     */
    public void updateBoundingBox(int width, int height) {
        boundingBoxWidth = width;
        boundingBoxHeight = height;
    }

    /**
     * Resets the joystick position to the center.
     */
    public void reset() {
        setRelativeValue(new Vector2D());
    }

    /**
     * Gets the bounding box width of the control elements view.
     * @return The bounding box width.
     */
    public int getBoundingBoxWidth() {
        return boundingBoxWidth;
    }

    /**
     * Gets the bounding box height of the control elements view.
     * @return The bounding box height.
     */
    public int getBoundingBoxHeight() {
        return boundingBoxHeight;
    }

    /**
     * Gets the center position of the bounding box and the overall joystick.
     * @return The center position of the bounding box.
     */
    public Point getCenter() {
        return new Point(boundingBoxWidth / 2, boundingBoxHeight / 2);
    }

    /**
     * Sets the relative control value in range [-1,1] relative to the center.
     * @param relativeValue The relativeValue.
     */
    public synchronized void setRelativeValue(Vector2D relativeValue) {
        this.relativeValue = relativeValue;
    }

    /**
     * Gets the relative control value in range [-1,1] relative to the center.
     * @return relativeValue The relativeValue.
     */
    public synchronized Vector2D getRelativeValue() {
        return relativeValue;
    }

    /**
     * Gets the draggable stick image.
     * @return The stick image.
     */
    public Bitmap getStickBitmap() {
        return stickBitmap;
    }

    /**
     * Gets the background image.
     * @return The background image.
     */
    public Bitmap getBackgroundBitmap() {
        return backgroundBitmap;
    }

    /**
     * Ges the control stick bitmap width.
     * @return The control stick bitmap width.
     */
    public int getStickWidth() {
        return stickBitmap.getWidth();
    }

    /**
     * Ges the control stick bitmap height.
     * @return The control stick bitmap height.
     */
    public int getStickHeight() {
        return stickBitmap.getHeight();
    }

    /**
     * Gets the background bitmap width.
     * @return The background bitmap width.
     */
    public int getBackgroundWidth() {
        return backgroundBitmap.getWidth();
    }

    /**
     * Gets the background bitmap height.
     * @return The background bitmap height.
     */
    public int getBackgroundHeight() {
        return backgroundBitmap.getHeight();
    }
}
