package de.tum.in.mislcontrol.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * A simple resize animation that can be applied to a view.
 */
public class ResizeAnimation extends Animation {
    private int targetWidth;
    private int startWidth;
    private int targetHeight;
    private int startHeight;
    private View view;

    public ResizeAnimation(View view, int targetWidth, int targetHeight)
    {
        this.view = view;
        this.targetWidth = targetWidth;
        startWidth = view.getWidth();
        this.targetHeight = targetHeight;
        startHeight = view.getHeight();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        int newWidth = startWidth + (int) ((targetWidth - startWidth) * interpolatedTime);
        int newHeight = startHeight + (int) ((targetHeight - startHeight) * interpolatedTime);

        view.getLayoutParams().width = newWidth;
        view.getLayoutParams().height = newHeight;
        view.requestLayout();
    }

    @Override
    public boolean willChangeBounds()
    {
        return true;
    }
}
