package de.tum.in.mislcontrol.controls;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * The model class of a joystick.
 */
public class JoystickModel {

    /**
     * The maximum control value.
     */
    public static final int MAX_VALUE = 255;

    /**
     * The bounding box width of the view.
     */
    private int boundingBoxWidth;

    /**
     * The bounding box height of the view.
     */
    private int boundingBoxHeight;

    /**
     * The x position of the joystick relative to the top-left.
     */
    private volatile Point touchPosition = new Point();

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
    public JoystickModel(Resources res, int stickResource, int backgroundResource) {
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
        setStickX(0);
        setStickY(0);
    }

    /**
     * Gets the stick position relative to the center. It is ensured that the position is in bounds
     * of the background image.
     * @return The vector in screen pixel coordinates relative to the center.
     */
    private Vector2D getStickVectorRelativeToCenterInBounds() {
        Vector2D value = new Vector2D(touchPosition.x - getCenter().x, touchPosition.y - getCenter().y);
        double length = value.length();
        double backgroundRadius = getBackgroundWidth() / 2; // we assume width and height are equal
        double scaleFactor = length;
        if (length > backgroundRadius) {
            scaleFactor = backgroundRadius;
        }
        Vector2D normalizedVector = value.normalize();
        return normalizedVector.scale(scaleFactor);
    }

    /**
     * Gets the bounding box width of the joystick.
     * @return The bounding box width.
     */
    public int getBoundingBoxWidth() {
        return boundingBoxWidth;
    }

    /**
     * Gets the bounding box height of the joystick.
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
     * Gets the position of the stick x coordinate relative to the left. It could be different to
     * rendered value, because this just represents the users finger position.
     * @return The stick position on the x axis based on the users finger.
     */
    public Point getTouchPosition() {
        return touchPosition;
    }

    /**
     * Sets the touch position relative to the top-left of the view element.
     * @param touchPosition The touch position.
     */
    public synchronized void setTouchPosition(Point touchPosition) {
        this.touchPosition = touchPosition;
    }

    /**
     * Gets the stick x position relative to the center.
     * @return The stick x position.
     */
    public synchronized double getStickX() {
        Vector2D vector = getStickVectorRelativeToCenterInBounds();
        return vector.getX();
    }

    /**
     * Sets the stick x position relative to the center.
     * @param x The stick x position.
     */
    public synchronized void setStickX(double x) {
        touchPosition.x = (int)(getCenter().x + x);
    }

    /**
     * Gets the stick y position relative to the center.
     * @return The stick y position.
     */
    public synchronized double getStickY() {
        Vector2D vector = getStickVectorRelativeToCenterInBounds();
        return vector.getY();
    }

    /**
     * Sets the stick y position relative to the center.
     * @param y The stick y position.
     */
    public synchronized void setStickY(double y) {
        touchPosition.y = (int)(getCenter().y + y);
    }

    /**
     * Gets the control value that could be transmitted to ASEP.
     * @return The control value.
     */
    public synchronized Vector2D getValue() {
        Vector2D vector = getStickVectorRelativeToCenterInBounds();
        double backgroundRadius = getBackgroundWidth() / 2; // we assume width and height are equal
        return vector.scale(MAX_VALUE / backgroundRadius);
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
