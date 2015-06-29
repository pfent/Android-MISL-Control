package de.tum.in.mislcontrol;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoStreamReceiverFragment extends Fragment {


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
        // Grab instance of WebView
        if (rootView != null) {
            WebView webView = (WebView)rootView.findViewById(R.id.webViewer);
            // Set page content for webview
            // NOTE: this HTML code can also be used in a BROWSER
            webView.loadData("<html><head><meta name='viewport' content='target-densitydpi=device-dpi,initial-scale=1,minimum-scale=1,user-scalable=yes'/></head><body><center><img src=\"http://192.168.1.124:6789/\" alt=\"Stream\" align=\"middle\"></center></body></html>", "text/html", null);
            webView.getSettings().setBuiltInZoomControls(true);
        }
    }
}
