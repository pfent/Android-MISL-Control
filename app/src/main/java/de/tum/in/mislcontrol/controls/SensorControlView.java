package de.tum.in.mislcontrol.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import de.tum.in.mislcontrol.R;

/**
 * A sensor control view element based on a SurfaceView.
 */
public class SensorControlView extends SurfaceView implements IRenderable, IController, SurfaceHolder.Callback {
    /**
     * The thread to render all the stuff.
     */
    private RenderThread renderThread;

    /**
     * The sensor control model.
     */
    private final SensorControlModel sensorControlModel;

    /**
     * The sensor control controller.
     */
    private final SensorControlController sensorControlController;

    /**
     * Creates a virtual joystick view instance.
     * @param context The android context.
     */
    public SensorControlView(Context context) {
        this(context, null, 0);
    }

    /**
     * Creates a virtual joystick view instance.
     * @param context The android context.
     * @param attrs The attribute set.
     */
    public SensorControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates a virtual joystick view instance.
     * @param context The android context.
     * @param attrs The attribute set.
     * @param defStyle the default style.
     */
    public SensorControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getHolder().addCallback(this);

        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);

        sensorControlModel = new SensorControlModel(getResources(), R.drawable.joystick_control, R.drawable.sensor_control_background);
        sensorControlController = new SensorControlController(context, sensorControlModel);
        setOnTouchListener(sensorControlController);
        renderThread = new RenderThread(getHolder(), this);
        setFocusable(true);
    }

    @Override
    public void render(Canvas canvas) {
        if (canvas == null)
            return;

        // draw the joystick background
        canvas.drawBitmap(sensorControlModel.getBackgroundBitmap(),
                getWidth() / 2 - sensorControlModel.getBackgroundWidth() / 2,
                getWidth() / 2 - sensorControlModel.getBackgroundHeight() / 2,
                null);

        // draw the draggable joystick
        canvas.drawBitmap(sensorControlModel.getStickBitmap(),
                (sensorControlModel.getCenter().x - sensorControlModel.getStickWidth() / 2),
                (int) (sensorControlModel.getCenter().y + sensorControlModel.getStickY() - sensorControlModel.getStickHeight() / 2),
                null);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(renderThread.getRenderState() == RenderThread.RenderState.PAUSED){
            //When game is opened again in the Android OS
            renderThread = new RenderThread(getHolder(),this);
        }
        renderThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        sensorControlModel.updateBoundingBox(width, height);
        sensorControlController.update(null);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        renderThread.terminate();
    }

    @Override
    public Vector2D getValue() {
        return sensorControlModel.getValue();
    }
}
