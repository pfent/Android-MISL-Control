package de.tum.in.mislcontrol.controls;

import de.tum.in.mislcontrol.math.Vector2D;

public class MockController implements IInputController {
    @Override
    public Vector2D getValue() {
        return new Vector2D(1,0);
    }
}
