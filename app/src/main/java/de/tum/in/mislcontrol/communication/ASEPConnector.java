package de.tum.in.mislcontrol.communication;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.tum.in.mislcontrol.ASEPAdapter;
import de.tum.in.mislcontrol.communication.data.CommandPacket;
import de.tum.in.mislcontrol.communication.data.TelemetryPacket;
import de.tum.in.mislcontrol.controls.IInputController;
import de.tum.in.mislcontrol.math.Vector2D;

/**
 * The ASEP connector implementations to send commands and receive status information.
 */
public class ASEPConnector implements IConnector {
    //TODO use preferences
    private static final int DEFAULT_PORT = 30190;
    private static final byte[] DEFAULT_BYTE_ADDRESS = {(byte) 192, (byte) 168, (byte) 16, (byte) 254};
    public static final String WIFI_SSID = "MISL_ROBOT_WPA";

    private static InetAddress inetAddress;
    private OnTelemetryReceivedListener receiver;
    private IInputController inputController;
    private final CommandPacket sending = new CommandPacket();
    private DatagramSocket sock;
    private ScheduledExecutorService schedulerService;
    private boolean listening = false;
    private short ch1, ch2;

    /**
     * Creates a ASEP connector instance to send commands and receive status information.
     */
    public ASEPConnector() {
        try {
            inetAddress = InetAddress.getByAddress(DEFAULT_BYTE_ADDRESS);
            //explicitly set SO_REUSEADDR, so we don't get EADDRINUSE
            sock = new DatagramSocket(null);
            sock.setReuseAddress(true);
            sock.bind(new InetSocketAddress(DEFAULT_PORT));
            sock.setSoTimeout(DEFAULT_TIMEOUT);
        } catch (UnknownHostException | SocketException e) {
            Log.e("ASEPConnector", "Unexpected Exception while initializing", e);
        }
    }

    @Override
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
        //don't increase sequence count, since the windows GUI also doesn't
        //sending.increaseSeqCnt();
        sending.calculateChecksum();
        DatagramPacket command =
                new DatagramPacket(sending.getData(), sending.getLength(), inetAddress, DEFAULT_PORT);
        sock.send(command);
    }

    @Override
    public synchronized void start() {
        if (listening || sock == null) {
            return;
        }
        listening = true;

        // Since ASEP does not reliably respond to our packets, we need asynchronous send/receive
        // Send packets in 25ms intervals, so we properly trigger responses, even when ASEP looses
        // some of our commands
        schedulerService = Executors.newScheduledThreadPool(1);
        schedulerService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (inputController != null) {
                        Vector2D direction = inputController.getValue();
                        Pair<Short, Short> channels = ASEPAdapter.drive(direction.getX(), direction.getY());
                        setCommand(channels.first, channels.second);
                        //Log.d("ASEPConnector", "Joystick returned x:" + direction.getX() + ", y:" + direction.getY());
                    }
                    sendCommand(ch1, ch2);
                    Log.d("ASEPConnector", "Sent command ch1:" + ch1 + ", ch2:" + ch2);

                } catch (IOException e) {
                    Log.e("ASEPConnector", "Unexpected Exception while sending", e);
                }
            }
        }, 0, DEFAULT_INTERVAL, TimeUnit.MILLISECONDS);

        //Continuously listen for telemetry
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean timedOut = false;

                while (listening) {
                    try {
                        //Wait for the response packet
                        TelemetryPacket nextTelemetry = new TelemetryPacket();
                        DatagramPacket packet =
                                new DatagramPacket(nextTelemetry.getData(), nextTelemetry.getLength());
                        sock.receive(packet);

                        //ignore sequence count, since ASEP does not update them
                        timedOut = false;
                        receiver.onTelemetryReceived(nextTelemetry);
                    } catch (InterruptedIOException e) {
                        //this means the DEFAULT_TIMEOUT expired, connection has been lost
                        if (!timedOut) {
                            timedOut = true;
                            receiver.onTelemetryTimedOut();
                        }
                    } catch (IOException e) {
                        Log.e("ASEPConnector", "Unexpected Exception while recieving", e);
                    }
                }
            }
        }).start();
    }

    @Override
    public synchronized void stop() {
        listening = false;
        if(schedulerService != null)
            schedulerService.shutdown();
    }

    @Override
    public boolean checkConnection(Context context) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo != null && wifiInfo.getSSID() != null &&
                wifiInfo.getSSID().contains(WIFI_SSID);
    }

    @Override
    public void setOnTelemetryReceivedListener(OnTelemetryReceivedListener receiver) {
        this.receiver = receiver;
    }

    @Override
    public void setInputController(IInputController controller) {
        this.inputController = controller;
    }

    @Override
    public void close() {
        stop();
        if (sock != null) {
            sock.close();
        }
    }
}
