package de.tum.in.mislcontrol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.parser.IParser;
import min3d.parser.Parser;


/**
 * The fragment for rendering the 3D model of ASEP.
 */
public class Model3DFragment extends Fragment { //TODO: Create a RenderingFrament like RenderingActivity of min3d engine

    /**
     * The 3D model object.
     */
    private Object3dContainer object3d;

    public Model3DFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_model3d, container, false);
    }
}
