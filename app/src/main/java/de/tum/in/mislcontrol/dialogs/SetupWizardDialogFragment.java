package de.tum.in.mislcontrol.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import de.tum.in.mislcontrol.MainActivity;
import de.tum.in.mislcontrol.R;
import de.tum.in.mislcontrol.SettingsActivity;

/**
 * Created by Benjamin on 04.06.2015.
 */
public class SetupWizardDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.fragment_dialog_setup_wizard, null))
                // Add action buttons
                .setPositiveButton(R.string.setup, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SetupWizardDialogFragment.this.getDialog().cancel();

                        // forward to settings activity
                        Intent intent = new Intent(getActivity(), SettingsActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.skip, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SetupWizardDialogFragment.this.getDialog().cancel();

                        // forward to main activity
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                });
        return builder.create();
    }
}
