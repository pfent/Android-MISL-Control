package de.tum.in.mislcontrol.controls;

/**
 * A simple immutable 2D vector implementation.
 */
public class Vector2D {
    private final double x;
    private final double y;

    /**
     * Creates a 2D vector instance.
     */
    public Vector2D() {
        x = 0;
        y = 0;
    }

    /**
     * Creates a 2D vector instance.
     * @param x The x value.
     * @param y The y value.
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vector2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector2D vector2D = (Vector2D) o;
        return Double.compare(vector2D.x, x)==0 && Double.compare(vector2D.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * Computes the magnitude of the 2D vector.
     * @return The magnitude.
     */
    public double length() {
        return Math.sqrt ( x * x + y * y);
    }

    /**
     * Computes the squared magnitude of the 2D vector as an optimization
     * of {@link de.tum.in.mislcontrol.controls.Vector2D#length}.
     * @return The squared magnitude.
     */
    public double lengthSquared() {
        return x * x + y * y;
    }

    // Sum of two vectors ....

    /**
     * Adds another 2D vector to it.
     * @param other The other 2D vector.
     * @return The resulting vector.
     */
    public Vector2D add(Vector2D other) {
        return new Vector2D( x + other.x, y + other.y);
    }

    /**
     * Subtracts another 2D vector to it.
     * @param other The other 2D vector.
     * @return The resulting vector.
     */
    public Vector2D sub(Vector2D other) {
        return new Vector2D( x - other.x, y - other.y);
    }

    /**
     * Scales the 2D vector by a constant factor.
     * @param scaleFactor The scale factor
     * @return The resulting vector.
     */
    public Vector2D scale(double scaleFactor) {
        return new Vector2D( x * scaleFactor, y * scaleFactor);
    }

    /**
     * Normalized the vector.
     * @return The resulting vector.
     */
    public Vector2D normalize() {
        double length = length();
        if (length != 0) {
            return new Vector2D(x / length, y / length);
        }
        return new Vector2D();
    }

    /**
     * Calculates the dot product of two vectors.
     * @param other The other vector.
     * @return The resulting vector.
     */
    public double dotProduct ( Vector2D other ) {
        return x *other.x + y *other.y;
    }

    /**
     * Gets the x value.
     * @return The x value.
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y value.
     * @return The y value.
     */
    public double getY() {
        return y;
    }
}
