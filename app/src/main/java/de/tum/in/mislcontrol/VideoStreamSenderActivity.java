package de.tum.in.mislcontrol;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;

import de.tum.in.mislcontrol.camera.CameraPreview;


public class VideoStreamSenderActivity extends ActionBarActivity {

    private static final String LOG_TAG = "VideoStreamSender";

    TextView connectionStatus;

    MediaRecorder recorder;
    public static String SERVERIP = "192.168.1.123";
    public static final int SERVERPORT = 6789;
    private Handler handler = new Handler();
    private ServerSocket serverSocket;

    private Camera camera;
    private CameraPreview cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_stream_sender);

        // Define UI elements
        connectionStatus = (TextView) findViewById(R.id.connection_status_textview);

        // Run new thread to handle socket communications
        Thread sendVideo = new Thread(new SendVideoThread());
        sendVideo.start();

    }

    /**
     * Gets the camera instance.
     * @return The camera instance.
     */
    public Camera getCameraInstance(){
        try {
            return Camera.open();
        }
        catch (Exception e) {
            Log.w(LOG_TAG, "Error: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Create an instance of Camera
        camera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        cameraPreview = new CameraPreview(this, camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (camera != null){
            camera.release();
            camera = null;
        }

        closeServerSocket();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_stream_sender, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i(LOG_TAG, "***** IP="+ ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(LOG_TAG, ex.toString());
        }
        return null;
    }

    public class SendVideoThread implements Runnable{
        private final static String LOG_TAG = "SendVideoThread";

        public void run(){
            // From Server.java
            try {
                if(SERVERIP != null){
                    WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
                    final String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                    InetAddress addr = InetAddress.getByName(ip);
                    serverSocket = new ServerSocket(SERVERPORT, 50, addr);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            connectionStatus.setText("Listening on IP: " + ip + " Port: " + SERVERPORT);
                        }
                    });
                    //SocketAddress address2 = serverSocket.getLocalSocketAddress();
                    while(true) {
                        //listen for incoming clients
                        Socket client = serverSocket.accept();
                        handler.post(new Runnable(){
                            @Override
                            public void run(){
                                connectionStatus.setText("Connected.");
                            }
                        });
                        try{
                            // Begin video communication
                            final ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(client);
                            handler.post(new Runnable(){
                                @Override
                                public void run(){
                                    recorder = new MediaRecorder();
                                    recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                    recorder.setOutputFile(pfd.getFileDescriptor());
                                    //recorder.setVideoFrameRate(20); http://stackoverflow.com/questions/11249642/mediarecorder-start-failed-19
                                    recorder.setVideoSize(176,144);
                                    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
                                    recorder.setPreviewDisplay(cameraPreview.getSurface());
                                    try {
                                        recorder.prepare();
                                    } catch (IllegalStateException e) {
                                        Log.w("LOG_TAG", "Illegal state.");
                                    } catch (IOException e) {
                                        Log.w("LOG_TAG", "IO error.");
                                    }
                                    recorder.start();
                                }
                            });
                        } catch (Exception e) {
                            handler.post(new Runnable(){
                                @Override
                                public void run(){
                                    connectionStatus.setText("Oops.Connection interrupted. Please reconnect your phones.");
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run(){
                            connectionStatus.setText("Couldn't detect internet connection.");
                        }
                    });
                }
            } catch (Exception e){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        connectionStatus.setText("Error");
                    }
                });
                e.printStackTrace();
            }
            finally {
                closeServerSocket();
            }
            // End from server.java
        }
    }
}
