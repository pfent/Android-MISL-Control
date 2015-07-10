package de.tum.in.mislcontrol.wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.codepond.wizardroid.WizardStep;

import de.tum.in.mislcontrol.R;

/**
 * The wizards page third step.
 */
public class Step3 extends WizardStep {

    /*Thomas Le Bas*/

    //Wire the layout to the step
    public Step3() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wizard_instructionstep, container, false);
        TextView tv = (TextView) v.findViewById(R.id.instructionTextView);
        tv.setText(R.string.wizard_step_3);
        return v;
    }
}
