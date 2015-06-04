package de.tum.in.android_misl_control.connection;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ASEPConnector {
    private static final int PORT = 30190;
    private static final byte[] BYTE_ADDRESS = {(byte) 192, (byte) 168, (byte) 16, (byte) 254};
    private static final int TIMEOUT = 2 * 1000; // 2 Seconds
    private static final int INTERVAL = 250; //25ms interval
    private static InetAddress inetAddress;
    private TelemetryReceivedListener receiver;
    private CommandPacket sending = new CommandPacket();
    private TelemetryPacket receiving = new TelemetryPacket();
    private DatagramSocket sock;
    private boolean listening = false;
    private short ch1, ch2;

    /**
     * Interface for receiving TelemetryPackets
     * Its functions are called asynchronously. Remember to use runOnUiThread
     */
    public interface TelemetryReceivedListener {
        /**
         * This function gets called, when a new packet has been recieved
         *
         * @param packet The Packet received
         */
        void onTelemetryReceived(TelemetryPacket packet);

        /**
         * This function gets called, when the TelemetryPacket times out, but only once for every
         * sequence of uninterrupted timeouts
         */
        void onTelemetryTimedOut();
    }

    /**
     * Create a new
     *
     * @param receiver Callbacks for received packets
     */
    ASEPConnector(TelemetryReceivedListener receiver) {
        this.receiver = receiver;
        try {
            inetAddress = InetAddress.getByAddress(BYTE_ADDRESS);
            sock = new DatagramSocket(PORT);
            sock.setSoTimeout(TIMEOUT);
        } catch (UnknownHostException | SocketException e) {
            //TODO should not be thrown, probably do some logging
        }
    }

    /**
     * Set the movement command for ASEP, which gets sent in the next packet to ASEP
     *
     * @param ch1 Channel 1 command, (left?) wheel speed. Ranges -6 to 6
     * @param ch2 Channel 2 command, (right?) wheel speed. Ranges -6 to 6
     */
    public synchronized void setCommand(short ch1, short ch2) {
        this.ch1 = ch1;
        this.ch2 = ch2;
    }

    /**
     * Send the movement command to ASEP
     *
     * @param ch1 Channel 1 command, (left?) wheel speed. Ranges -6 to 6
     * @param ch2 Channel 2 command, (right?) wheel speed. Ranges -6 to 6
     * @throws IOException When the connection failes
     */
    private synchronized void sendCommand(short ch1, short ch2) throws IOException {
        sending.setCH1Cmd(ch1);
        sending.setCH2Cmd(ch2);
        sending.increaseSeqCnt();
        sending.calculateChecksum();
        DatagramPacket command =
                new DatagramPacket(sending.command, sending.command.length, inetAddress, PORT);
        sock.send(command);
    }

    /**
     * Periodically sends the latest commands from setCommand() and notifies the associated
     * TelemetryReceivedListener about incoming response packets or timeouts
     */
    public synchronized void start() {
        if (listening || sock == null) {
            return;
        }
        listening = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean timedOut = false;
                while (listening) {
                    try {
                        sendCommand(ch1, ch2);

                        //Wait for the response packet
                        TelemetryPacket next = new TelemetryPacket();
                        DatagramPacket packet =
                                new DatagramPacket(next.telemetry, next.telemetry.length);
                        sock.receive(packet);

                        //unsigned comparison
                        if (next.getSeqCont() - receiving.getSeqCont() > 0) {
                            timedOut = false;
                            receiving = next;
                            receiver.onTelemetryReceived(next);
                        }

                        Thread.sleep(INTERVAL);
                    } catch (InterruptedIOException e) {
                        //this means the TIMEOUT expired, connection has been lost
                        if (!timedOut) {
                            timedOut = true;
                            receiver.onTelemetryTimedOut();
                        }
                    } catch (IOException | InterruptedException e) {
                        //TODO probably do some logging
                    }
                }
            }
        }).start();
    }

    /**
     * Stops sending and receiving of packets.
     * The callbacks in TelemetryReceivedListener may get called a last time.
     */
    public synchronized void stop() {
        listening = false;
    }

    /**
     * Release all resources
     */
    public void close() {
        stop();
        if (sock != null) {
            sock.close();
        }
    }
}
