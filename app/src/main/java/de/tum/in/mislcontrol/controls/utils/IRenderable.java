package de.tum.in.mislcontrol.controls.utils;

import android.graphics.Canvas;

/**
 * The interface for view elements that can be rendered on a canvas.
 */
public interface IRenderable {
    /**
     * Renders the visual element.
     * @param canvas The canvas to render on.
     */
     void render(Canvas canvas);
}
