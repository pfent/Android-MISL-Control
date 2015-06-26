package de.tum.in.mislcontrol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tum.in.mislcontrol.model3d.RenderFragment;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;


/**
 * The fragment for rendering the 3D model of ASEP.
 */
public class Model3DFragment extends RenderFragment {

    private Object3dContainer objModel;

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

            scene.camera().position.z += 2.5f;
        }
    }

    @Override
    public void updateScene() {
        if (objModel == null)
            return;

        objModel.rotation().x++;
        objModel.rotation().z++;

    }
}
