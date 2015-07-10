package de.tum.in.mislcontrol;

import android.util.Log;

import de.tum.in.mislcontrol.math.MathHelper;
import de.tum.in.mislcontrol.model3d.IModel3dView;
import de.tum.in.mislcontrol.model3d.RenderFragment;
import min3d.core.Object3dContainer;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;
import min3d.vos.Number3d;


/**
 * The fragment for rendering the 3D model of ASEP.
 */
public class Model3DFragment extends RenderFragment implements IModel3dView {
    private static final String LOG_TAG = "Model3DFragment";

    /**
     * The alpha value of the exponential filter for smooth rotation.
     */
    private final static double FILTER_ALPHA = 0.05f;

    /**
     * The cameras distance from the 3D model.
     */
    private final static float CAMERA_DISTANCE = 7.5f;

    /**
     * The 3d model container.
     */
    private Object3dContainer objModel;

    /**
     * The current roll value (x CW).
     */
    private float roll = (float)Math.PI;

    /**
     * The current pitch value (z CCW).
     */
    private float pitch = 0;

    /**
     * The current yaw value (y CCW).
     */
    private float yaw = 0;

    /**
     * The target roll value in interval [-2* PI, 4*PI].
     */
    private float targetRoll = roll;

    /**
     * The target pitch value in interval [-2 * PI, 4*PI].
     */
    private float targetPitch = pitch;

    /**
     * The target yaw value in interval [-2 * PI, 4*PI].
     */
    private float targetYaw = yaw;

    @Override
    public void initScene() {
        scene.lights().add(new Light());

        IParser parser = Parser.createParser(Parser.Type.OBJ, getResources(), String.format("%s:%s", getActivity().getPackageName(), "raw/asep_obj") , false);
        if (parser != null)
        {
            parser.parse();

            objModel = parser.getParsedObject();
            objModel.scale().x = objModel.scale().y = objModel.scale().z = .1f;
            scene.addChild(objModel);

            // rotate camera so that it is located in third person view
            scene.camera().position.z = 0f;
            scene.camera().position.x = -CAMERA_DISTANCE;
            scene.camera().position.y = -0.5f;
            scene.camera().upAxis = new Number3d(0, -1, 0);
        }
    }

    @Override
    public synchronized void updateScene() {
        if (objModel == null)
            return;

        // calculate next rotation values due to filtering
        float nextRoll = (float)MathHelper.exponentialFilter(roll, targetRoll, FILTER_ALPHA);
        float nextPitch = (float)MathHelper.exponentialFilter(pitch, targetPitch, FILTER_ALPHA);
        float nextYaw = (float)MathHelper.exponentialFilter(yaw, targetYaw, FILTER_ALPHA);
        // try to adjust the values back in range
        if (targetRoll < 0 && roll < 0) {
            targetRoll += MathHelper.PI_2;
            roll += MathHelper.PI_2;
            nextRoll += MathHelper.PI_2;
        } else if (targetRoll >= MathHelper.PI_2 && roll >= MathHelper.PI_2) {
            targetRoll -= MathHelper.PI_2;
            roll -= MathHelper.PI_2;
            nextRoll -= MathHelper.PI_2;
        }
        if (targetPitch < 0 && pitch < 0) {
            targetPitch += MathHelper.PI_2;
            pitch += MathHelper.PI_2;
            nextPitch += MathHelper.PI_2;
        } else if (targetPitch >= MathHelper.PI_2 && pitch >= MathHelper.PI_2) {
            targetPitch -= MathHelper.PI_2;
            pitch -= MathHelper.PI_2;
            nextPitch -= MathHelper.PI_2;
        }
        if (targetYaw < 0 && yaw < 0) {
            targetYaw += MathHelper.PI_2;
            yaw += MathHelper.PI_2;
            nextYaw += MathHelper.PI_2;
        } else if (targetYaw >= MathHelper.PI_2 && yaw >= MathHelper.PI_2) {
            targetYaw -= MathHelper.PI_2;
            yaw -= MathHelper.PI_2;
            nextYaw -= MathHelper.PI_2;
        }

        this.roll = nextRoll;
        this.pitch = nextPitch;
        this.yaw = nextYaw;

        objModel.rotation().x = (float)Math.toDegrees(this.pitch);
        objModel.rotation().z = (float)Math.toDegrees(this.roll);

        // rotate camera to simulate YAW
        scene.camera().position.x = CAMERA_DISTANCE * (float)Math.cos(this.yaw);
        scene.camera().position.z = CAMERA_DISTANCE * (float)Math.sin(this.yaw);
    }

    /**
     * Sets the rotation of the model.
     * @param roll The roll (x)
     * @param pitch The pitch (z)
     * @param yaw The yaw (z)
     */
    @Override
    public synchronized void setRotation(float roll, float pitch, float yaw) {
        // ensure value in range [0, 2*PI]
        float positiveRoll = (roll < 0) ? (roll + MathHelper.PI_2) : roll;
        float positivePitch = (pitch < 0) ? (pitch + MathHelper.PI_2) : pitch;
        float positiveYaw = (yaw < 0) ? (yaw + MathHelper.PI_2) : yaw;

        // get current values to make (almost) sure they are consistent during calculation (to range [0, 2 * PI])
        float currentRoll = (this.roll < 0) ? (this.roll + MathHelper.PI_2) : this.roll;
        float currentPitch = (this.pitch < 0) ? (this.pitch + MathHelper.PI_2) : this.pitch;
        float currentYaw = (this.yaw < 0) ? (this.yaw + MathHelper.PI_2) : this.yaw;

        float minDiffRoll = (float)minDiff3(currentRoll, positiveRoll - MathHelper.PI_2, positiveRoll, positiveRoll + MathHelper.PI_2);
        float minDiffPitch = (float)minDiff3(currentPitch, positivePitch - MathHelper.PI_2, positivePitch, positivePitch + MathHelper.PI_2);
        float minDiffYaw = (float)minDiff3(currentYaw, positiveYaw - MathHelper.PI_2, positiveYaw, positiveYaw + MathHelper.PI_2);

        this.targetRoll = currentRoll + minDiffRoll;
        this.targetPitch = currentPitch + minDiffPitch;
        this.targetYaw = currentYaw + minDiffYaw;
    }

    /**
     * Gets the minimum difference from a value to tree other values.
     * @param from The value a.
     * @param value1 The value b1.
     * @param value2 The value b2.
     * @param value3 The value b3.
     * @return Returns the minimum difference.
     */
    private static double minDiff3(double from, double value1, double value2, double value3) {
        double diff1 = Math.abs(value1 - from);
        double diff2 = Math.abs(value2 - from);
        double diff3 = Math.abs(value3 - from);

        double minDiff = Math.min(diff1, Math.min(diff2, diff3));
        if (minDiff == diff1)
            return value1 - from;
        else if (minDiff == diff2)
            return value2 - from;
        else
            return value3 - from;
    }
}
