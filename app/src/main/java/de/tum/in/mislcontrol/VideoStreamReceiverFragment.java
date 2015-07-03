package de.tum.in.mislcontrol;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;

import de.tum.in.mislcontrol.camera.StreamProxy;


/**
 * A simple Framgent class for receiving the video.
 */
public class VideoStreamReceiverFragment extends Fragment implements SurfaceHolder.Callback {

    MediaPlayer mp;
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    private TextView mTextview;
    public static final int SERVERPORT = 6789;
    public static String SERVERIP = "192.168.178.53"; // TODO: use port from settings! Differs from device to device!

    private final Handler handler = new Handler();

    StreamProxy streamProxy;

    public VideoStreamReceiverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_stream_receiver, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        final View rootView = getView();
        if (rootView != null) {
            mPreview = (SurfaceView) rootView.findViewById(R.id.streamView);
            mTextview = (TextView) rootView.findViewById(R.id.infoText);
            holder = mPreview.getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mTextview.setText("Attempting to connect");
            streamProxy = new StreamProxy();
            mp = new MediaPlayer();
            Thread t = new Thread(){
                public void run(){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                streamProxy.start();
                                mp.setDataSource("http://127.0.0.1:8888/localfilepath"); // TODO how to use this?
                                mp.setDisplay(holder);
                                mp.prepareAsync();
                                mp.start();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    });
                }
            };
            t.start();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
