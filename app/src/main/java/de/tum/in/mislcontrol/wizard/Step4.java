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

public class Step4 extends WizardStep {

    //Wire the layout to the step
    public Step4() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wizard_instructionstep, container, false);
        TextView tv = (TextView) v.findViewById(R.id.instructionTextView);
        tv.setText("Connect the your WiFi to the WiFi of ASEP.");
        return v;
    }

    @Override
    public void onExit(int exitCode) {
       switch(exitCode) {
           case WizardStep.EXIT_NEXT:
               break;
           case WizardStep.EXIT_PREVIOUS: //This means skip for us
               Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
               startActivity(intent);
       }
    }
}
