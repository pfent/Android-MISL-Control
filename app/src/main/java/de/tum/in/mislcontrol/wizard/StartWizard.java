package de.tum.in.mislcontrol.wizard;

import org.codepond.wizardroid.WizardFlow;
import org.codepond.wizardroid.layouts.BasicWizardLayout;

/**
 * The start setup wizard to guide the users to connect to ASEP.
 */
public class StartWizard extends BasicWizardLayout {

    /**
     * Note that initially BasicWizardLayout inherits from {@link android.support.v4.app.Fragment}
     * and therefore you must have an empty constructor.
     */
    public StartWizard() {
        super();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public WizardFlow onSetup() {
        return new WizardFlow.Builder()
                .addStep(Step1.class)
                .addStep(Step2.class)
                .addStep(Step3.class)
                .addStep(Step4.class)
                .create();
    }

}
