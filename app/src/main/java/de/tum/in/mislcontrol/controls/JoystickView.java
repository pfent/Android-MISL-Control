package de.tum.in.mislcontrol.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import de.tum.in.mislcontrol.R;

/**
 * A virtual joystick view element based on a SurfaceView.
 */
public class JoystickView extends SurfaceView implements IRenderable, IController, SurfaceHolder.Callback {
    /**
     * The thread to render all the stuff.
     */
    private RenderThread renderThread;

    /**
     * The joystick model.
     */
    private JoystickModel joystickModel;

    /**
     * The joystick controller.
     */
    private JoystickController joystickController;

    /**
     * Creates a virtual joystick view instance.
     * @param context The android context.
     */
    public JoystickView(Context context) {
        this(context, null, 0);
    }

    /**
     * Creates a virtual joystick view instance.
     * @param context The android context.
     * @param attrs The attribute set.
     */
    public JoystickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates a virtual joystick view instance.
     * @param context The android context.
     * @param attrs The attribute set.
     * @param defStyle the default style.
     */
    public JoystickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getHolder().addCallback(this);
        joystickModel = new JoystickModel(getResources(), R.drawable.joystick_control, R.drawable.joystick_background);
        joystickController = new JoystickController(joystickModel);
        setOnTouchListener(joystickController);
        renderThread = new RenderThread(getHolder(), this);
        setFocusable(true);
    }

    @Override
    public void render(Canvas canvas) {
        if (canvas == null)
            return;

        // draw the joystick background
        canvas.drawBitmap(joystickModel.getBackgroundBitmap(),
                getWidth() / 2 - joystickModel.getBackgroundWidth() / 2,
                getWidth() / 2 - joystickModel.getBackgroundHeight() / 2,
                null);

        // draw the draggable joystick
        canvas.drawBitmap(joystickModel.getStickBitmap(),
                (int) (joystickModel.getCenter().x + joystickModel.getStickX() - joystickModel.getStickWidth() / 2),
                (int) (joystickModel.getCenter().y + joystickModel.getStickY() - joystickModel.getStickHeight() / 2),
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
        joystickModel.updateBoundingBox(width, height);
        joystickController.update(null);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        renderThread.terminate();
    }

    @Override
    public Vector2D getValue() {
        return joystickModel.getValue();
    }
}
