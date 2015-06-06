package de.tum.in.mislcontrol.controls;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The rendering thread class implementation to perform rendering of SurfaceView instances.
 */
public class RenderThread extends Thread {
    /**
     * The frame rate.
     */
    private static final int FPS = 15;

    /**
     * The holder of the surface.
     */
    private final SurfaceHolder surfaceHolder;

    /**
     * The enum of thread states.
     */
    public enum RenderState {
        RUNNING,
        PAUSED,
        STOPPED
    }

    /**
     * The thread state.
     */
    private RenderState state = RenderState.RUNNING;

    /**
     * The view to render.
     */
    private final IRenderable renderView;

    /**
     * Creats a new render thread instance.
     * @param surfaceHolder The surface holder.
     * @param renderView The view to render.
     */
    public RenderThread(SurfaceHolder surfaceHolder, IRenderable renderView){
        //data about the screen
        this.surfaceHolder = surfaceHolder;
        this.renderView = renderView;
    }

    @Override
    public void run() {

        Log.d(getClass().getName(), "Started");

        // render loop
        while (state== RenderState.RUNNING) {
            //time before update
            long beforeTime = System.nanoTime();

            Canvas c = null;
            try {
                //lock canvas so nothing else can use it
                c = surfaceHolder.lockCanvas(null);
                if (c != null) {
                    // render
                    synchronized (surfaceHolder) {
                        // clear screen
                        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        // render the view
                        renderView.render(c);
                    }
                }
            } finally {
                if (c != null) {
                    surfaceHolder.unlockCanvasAndPost(c);
                }
            }

            // sleep
            long sleepTime = 1000L / FPS -((System.nanoTime()-beforeTime)/1000000L);
            if(sleepTime > 0) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RenderThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            while (state== RenderState.PAUSED){
                Log.d(getClass().getName(), "Paused");
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    Log.w(getClass().getName(), "Interrupted");
                }
            }
        }
    }

    /**
     * Safely terminates the thread.
     */
    public void terminate() {
        boolean retry = true;
        // code to end rendering thread
        state = RenderState.STOPPED;
        while (retry) {
            try {
                // wait for thread finalization
                join();
                retry = false;
            } catch (InterruptedException e) {
                Log.w(getClass().getName(), "Interrupted");
            }
        }
    }

    /**
     * Gets the rendering state.
     * @return The rendering state.
     */
    public RenderState getRenderState() {
        return state;
    }
}
