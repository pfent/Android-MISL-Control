package de.tum.in.mislcontrol.wizard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.codepond.wizardroid.WizardStep;

import de.tum.in.mislcontrol.MainActivity;
import de.tum.in.mislcontrol.R;

public class StepFinish extends WizardStep {

    /**
     * The handler for delayed events.
     */
    private final Handler delayedActionHandler = new Handler();

    //Wire the layout to the step
    public StepFinish() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wizard_instructionstep, container, false);
        TextView tv = (TextView) v.findViewById(R.id.instructionTextView);
        tv.setText("FINISH!");
        return v;
    }

    @Override
    public void onExit(int exitCode) {
       switch(exitCode) {
           case WizardStep.EXIT_NEXT:
               // forward to main activity
               getActivity().setContentView(R.layout.activity_start);
               delayedActionHandler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                       startActivity(intent);
                   }
               }, 2000);
               break;
           case WizardStep.EXIT_PREVIOUS: //This means skip for us
               break;
       }
    }
}
