package de.tum.in.mislcontrol.controls;

import de.tum.in.mislcontrol.math.Vector2D;

/**
 * Interface for all controller implementations.
 */
public interface IInputController {
    /**
     * Gets the control value that could be transmitted to ASEP.
     * @return The control value.
     */
    Vector2D getValue();

    /**
     * Gets the input controler type name.
     * @return The input controller type name.
     */
    String getType();
}
