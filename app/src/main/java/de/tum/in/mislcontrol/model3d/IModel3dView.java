package de.tum.in.mislcontrol.model3d;

/**
 * The interface for a view that shows a 3D model.
 */
public interface IModel3dView {
    /**
     * Sets the rotation of the 3D model.
     * @param roll The roll value.
     * @param pitch The pitch value.
     * @param yaw The yaw value.
     */
    void setRotation(float roll, float pitch, float yaw);
}
