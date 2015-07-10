package de.tum.in.mislcontrol.wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.codepond.wizardroid.WizardStep;

import de.tum.in.mislcontrol.R;

/**
 * The wizards page first step.
 */
public class Step1 extends WizardStep {

    //Wire the layout to the step
    public Step1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wizard_instructionstep, container, false);
        TextView tv = (TextView) v.findViewById(R.id.instructionTextView);
        tv.setText(getText(R.string.wizard_step_1));

        ImageView iv = (ImageView) v.findViewById(R.id.instructionImageView);
        iv.setImageResource(R.drawable.battery);
        return v;
    }
}
