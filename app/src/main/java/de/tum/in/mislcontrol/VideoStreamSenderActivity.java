package de.tum.in.mislcontrol;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import de.tum.in.mislcontrol.camera.CameraPreview;


public class VideoStreamSenderActivity extends AppCompatActivity {

    private static final String LOG_TAG = "VideoStreamSender";

    TextView connectionStatus;

    private MediaRecorder recorder;
    public static final String SERVERIP = "192.168.1.123";
    public static final int SERVERPORT = 6789;
    private final Handler handler = new Handler();
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

        if (recorder != null && camera != null) {
            // stop recording and release camera
            recorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            camera.lock();         // take camera access back from MediaRecorder
        }

        if (camera != null){
            camera.release();
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

    /**
     * Closes the server socket in a safe fasion.
     */
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

    /**
     * Releases the media recorder.
     */
    private void releaseMediaRecorder(){
        if (recorder != null) {
            recorder.reset();   // clear recorder configuration
            recorder.release(); // release the recorder object
            recorder = null;
            camera.lock();           // lock camera for later use
        }
    }

    /**
     * The thread to send the video as a UDP stream.
     */
    public class SendVideoThread implements Runnable{
        private final static String LOG_TAG = "SendVideoThread";

        public void run(){
            try {
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
                                // Steps from: http://developer.android.com/guide/topics/media/camera.html
                                recorder = new MediaRecorder();

                                // Step 1: Unlock and set camera to MediaRecorder
                                camera.unlock();
                                recorder.setCamera(camera);

                                // Step 2: Set sources
                                recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                                // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
                                //recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
                                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                                recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
                                //recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                //recorder.setVideoFrameRate(20); http://stackoverflow.com/questions/11249642/mediarecorder-start-failed-19
                                recorder.setVideoSize(176,144);
                                //recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);

                                // Step 4: Set output file
                                recorder.setOutputFile(pfd.getFileDescriptor());

                                // Step 5: Set the preview output
                                //recorder.setPreviewDisplay(cameraPreview.getSurface());
                                try {
                                    recorder.prepare();
                                } catch (IllegalStateException e) {
                                    Log.w("LOG_TAG", "Illegal state.");
                                    releaseMediaRecorder();
                                } catch (IOException e) {
                                    Log.w("LOG_TAG", "IO error.");
                                    releaseMediaRecorder();
                                }
                                //camera.lock();
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
        }
    }
}
