package de.tum.in.mislcontrol.wizard;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.codepond.wizardroid.WizardStep;

import java.util.List;

import de.tum.in.mislcontrol.MainActivity;
import de.tum.in.mislcontrol.R;
import de.tum.in.mislcontrol.communication.ASEPConnector;

public class Step4 extends WizardStep {

    /* WIFI ICON BY Björn Andersson */

    /**
     * The handler for delayed events.
     */
    private final Handler delayedActionHandler = new Handler();

    //Wire the layout to the step
    public Step4() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wizard_instructionstep, container, false);
        TextView tv = (TextView) v.findViewById(R.id.instructionTextView);
        tv.setText(R.string.wizard_step_4);
        ImageView iv = (ImageView) v.findViewById(R.id.instructionImageView);
        iv.setImageResource(R.drawable.wifi);

        WifiManager wifiMan = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiMan.setWifiEnabled(true);

        WifiConfiguration wifiC = new WifiConfiguration();
        wifiC.SSID = "\"" + ASEPConnector.WIFI_SSID + "\"";
        wifiC.preSharedKey = "\"" + "password" + "\"";

        //TODO:
        //wifiMan.addNetwork(wifiC);
        List<WifiConfiguration> configs = wifiMan.getConfiguredNetworks();
        if (configs != null)
            for (WifiConfiguration config : configs) {
                if (config.SSID != null && config.SSID.equals(wifiC.SSID)) {
                    wifiMan.disconnect();
                    wifiMan.enableNetwork(config.networkId, true);
                    wifiMan.reconnect();
                    notifyCompleted();
                    break;
                }
            }

        return v;
    }

    @Override
    public void onExit(int exitCode) {
        switch (exitCode) {
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
