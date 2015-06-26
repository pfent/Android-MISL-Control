package de.tum.in.mislcontrol;

import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;

public class Model3DActivity extends RendererActivity {

    private Object3dContainer objModel;

    @Override
    public void initScene() {
        scene.lights().add(new Light());

        // TODO change file name
        IParser parser = Parser.createParser(Parser.Type.OBJ, getResources(), String.format("%s:%s", getPackageName(), "raw/asepus_obj") , false);
        if (parser != null)
        {
            parser.parse();

            objModel = parser.getParsedObject();
            objModel.scale().x = objModel.scale().y = objModel.scale().z = .1f;
            scene.addChild(objModel);
        }
    }

    @Override
    public void updateScene() {
        if (objModel == null)
            return;

        objModel.rotation().x++;
        objModel.rotation().z++;

        scene.camera().position.z += 0.01f;
    }
}
