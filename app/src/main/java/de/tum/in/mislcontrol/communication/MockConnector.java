package de.tum.in.mislcontrol.communication;

import android.os.Handler;

import de.tum.in.mislcontrol.communication.data.TelemetryPacket;
import de.tum.in.mislcontrol.controls.IControlValue;

/**
 * Mock implementation of the connector, mainly for UI testing purposes.
 */
public class MockConnector implements IConnector {

    /**
     * The handler for repeated events.
     */
    private final Handler repeatHandler = new Handler();

    /**
     * Thread safe flag variable to indicate the active state for event generation.
     */
    private volatile boolean isActive = false;

    /**
     * The mock telemetry package to fire.
     */
    private final TelemetryPacket mockTelemetryPacket;

    /**
     * The telemetry received callback.
     */
    private OnTelemetryReceivedListener receiver;

    /**
     * The repeat callback of the handler for repeated events.
     */
    private final Runnable repeatCallback = new Runnable() {
        @Override
        public void run() {
            if (isActive) {
                // fire event
                if (receiver != null) {
                    updateTelemetryPacket(mockTelemetryPacket);
                    receiver.onTelemetryReceived(mockTelemetryPacket);
                }

                repeatHandler.postDelayed(this, DEFAULT_INTERVAL);
            }
        }

        /**
         * Slightly updates the telemetry packet to see some variatoin.
         * @param telemetryPacket The telemetry packet to update.
         */
        private void updateTelemetryPacket(TelemetryPacket telemetryPacket) {
            // TODO: implement setter methods in telemetry packet and update the values here...
        }
    };

    /**
     * Creates a mock connector instance.
     */
    public MockConnector() {
        mockTelemetryPacket = new TelemetryPacket();
        // TODO: init telemetry packet...
    }

    @Override
    public void setCommand(short ch1, short ch2) {
        // NOP
    }

    @Override
    public void start() {
        isActive = true;
        repeatHandler.postDelayed(repeatCallback, DEFAULT_INTERVAL);
    }

    @Override
    public synchronized void stop() {
        if (!isActive)
            return;

        isActive = false;
        repeatHandler.removeCallbacks(repeatCallback);
    }

    @Override
    public boolean checkConnection() {
        return true;
    }

    @Override
    public void setOnTelemetryReceivedListener(OnTelemetryReceivedListener receiver) {
        this.receiver = receiver;
    }

    @Override
    public void setIControlValue(IControlValue controller) {
        throw new NoSuchMethodError();
    }

    @Override
    public void close() {
        stop();
    }
}
