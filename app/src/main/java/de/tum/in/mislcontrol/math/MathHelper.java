package de.tum.in.mislcontrol.math;

/**
 * A helper class with some recurring math functions that are not provided
 * in @see java.lang.Math of the Java SDK.
 */
public class MathHelper {

    /**
     * Ensures the value is in a given range.
     * @param value The value to clamp.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The clamped value.
     */
    public static double between(double value, double min, double max) {
        if (value < min)
            value = min;
        else if (value > max)
            value = max;
        return value;
    }
}
