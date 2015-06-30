package de.tum.in.mislcontrol.wizard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.codepond.wizardroid.WizardStep;

import de.tum.in.mislcontrol.MainActivity;
import de.tum.in.mislcontrol.R;

public class Step3 extends WizardStep {

    //Wire the layout to the step
    public Step3() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wizard_instructionstep, container, false);
        TextView tv = (TextView) v.findViewById(R.id.instructionTextView);
        tv.setText("There is a small smd button on the MISL stack second board from the bottom. \n" +
                "Press this button to initialize ASEP.");
        return v;
    }
}
