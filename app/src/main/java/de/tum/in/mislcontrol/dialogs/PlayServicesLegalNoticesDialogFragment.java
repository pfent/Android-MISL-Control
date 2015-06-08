package de.tum.in.mislcontrol.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;

import de.tum.in.mislcontrol.MainActivity;
import de.tum.in.mislcontrol.R;
import de.tum.in.mislcontrol.SettingsActivity;

/**
 * The dialog fragment to display the "Legal Notices" which is required
 * whenever an app uses the Google Play Services.
 */
public class PlayServicesLegalNoticesDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // set legal notices text
        View view = inflater.inflate(R.layout.fragment_dialog_play_services_legal_notices, null);
        TextView legalNotesTextView = (TextView)view.findViewById(R.id.play_services_legal_notes_content);
        legalNotesTextView.setText(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getActivity().getApplicationContext()));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle(R.string.play_services_legal_notices_title)
                .setView(inflater.inflate(R.layout.fragment_dialog_play_services_legal_notices, null))
                // add action buttons
                .setPositiveButton(R.string.setup, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // just close it
                        PlayServicesLegalNoticesDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
