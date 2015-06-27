package de.tum.in.mislcontrol;

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

    /**
     * The 3d model container.
     */
    private Object3dContainer objModel;

    /**
     * The roll value (x CW).
     */
    private float roll = (float)Math.PI;

    /**
     * The pitch value (z CCW).
     */
    private float pitch = 0;

    /**
     * The yaw value (y CCW).
     */
    private float yaw = 0;

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
            scene.camera().position.x = -7.5f;
            scene.camera().position.y = -0.5f;
            scene.camera().upAxis = new Number3d(0, -1, 0);
        }
    }

    @Override
    public void updateScene() {
        if (objModel == null)
            return;

        objModel.rotation().x = (float)Math.toDegrees(-roll);
        objModel.rotation().y = (float)Math.toDegrees(yaw);
        objModel.rotation().z = (float)Math.toDegrees(pitch);
        // TODO check whether the rotations are correct?
        // TODO maybe some smooth interpolation insted of "hard" rotation?
    }

    /**
     * Sets the rotation of the model.
     * @param roll The roll (x)
     * @param pitch The pitch (z)
     * @param yaw The yaw (z)
     */
    @Override
    public void setRotation(float roll, float pitch, float yaw) {
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
    }
}
