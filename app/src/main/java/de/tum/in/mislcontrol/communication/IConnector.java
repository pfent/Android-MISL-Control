package de.tum.in.mislcontrol.communication;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;

/**
 * Robot connection interface.
 */
public interface IConnector {

    /**
     * The default timeout of 2 seconds.
     */
    int DEFAULT_TIMEOUT = 2 * 1000; // 2 Seconds

    /**
     * The default transmission interval of 25ms.
     */
    int DEFAULT_INTERVAL = 25;

    /**
     * Set the movement command for ASEP, which gets sent in the next packet to ASEP.
     *
     * @param ch1 Channel 1 command, (left?) wheel speed. Ranges -6 to 6
     * @param ch2 Channel 2 command, (right?) wheel speed. Ranges -6 to 6
     */
    void setCommand(short ch1, short ch2);

    /**
     * Periodically sends the latest commands from setCommand() and notifies the associated
     * @see de.tum.in.mislcontrol.communication.IConnector.OnTelemetryReceivedListener about
     * incoming response packets or timeouts.
     */
    void start();

    /**
     * Stops sending and receiving of packets.
     * The callbacks in TelemetryReceivedListener may get called a last time.
     */
    void stop();

    /**
     * Checks the connection to the ASEP robot.
     * @return Returns true when the connection to the ASEP robot has been established, else false.
     */
    boolean checkConnection();

    /**
     * Sets the telemetry receiver callback function.
     * @param receiver The listener to receive events.
     */
    void setOnTelemetryReceivedListener(OnTelemetryReceivedListener receiver);

    /**
     * Release all resources
     */
    void close();

    /**
     * Interface for receiving TelemetryPackets.
     * Its functions are called asynchronously. Remember to use runOnUiThread.
     */
    interface OnTelemetryReceivedListener {
        /**
         * This function gets called, when a new packet has been received.
         *
         * @param packet The Packet received
         */
        void onTelemetryReceived(TelemetryPacket packet);

        /**
         * This function gets called, when the TelemetryPacket times out, but only once for every
         * sequence of uninterrupted timeouts.
         */
        void onTelemetryTimedOut();
    }
}
