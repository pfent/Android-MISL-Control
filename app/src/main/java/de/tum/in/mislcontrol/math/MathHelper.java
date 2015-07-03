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

    /**
     * Enables interpolation using an exponential filtering.
     * @param currentValue The current value.
     * @param targetValue The target value.
     * @param alpha The alpha value
     * @return The filtered value.
     */
    public static double exponentialFilter(double currentValue, double targetValue, double alpha) {
        if (currentValue == targetValue)
            return currentValue;

        double delta = targetValue - currentValue;
        return currentValue + delta * alpha;
    }
}
