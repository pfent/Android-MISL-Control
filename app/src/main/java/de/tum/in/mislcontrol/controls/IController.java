package de.tum.in.mislcontrol.controls;

/**
 * Interface for all controller implementations.
 */
public interface IController {
    /**
     * Gets the control value that could be transmitted to ASEP.
     * @return The control value.
     */
    Vector2D getValue();
}
