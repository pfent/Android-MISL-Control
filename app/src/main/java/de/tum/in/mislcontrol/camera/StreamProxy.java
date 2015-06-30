package de.tum.in.mislcontrol.camera;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

/**
 * A tiny streaming proxy implementation, that downloads the data to a file that can be read
 * from a MediaPlayer instance.
 *
 * Code skeleton taken from:
 * http://stackoverflow.com/questions/5343730/mediaplayer-stutters-at-start-of-mp3-playback/
 */
public class StreamProxy implements Runnable {
    public static final String LOG_TAG = "StreamProxy";

    private static final int SERVER_PORT=8888;

    private Thread thread;
    private static boolean isRunning;
    private ServerSocket socket;
    private int port;

    public StreamProxy() {

        // Create listening socket
        try {
            socket = new ServerSocket(SERVER_PORT, 0, InetAddress.getByAddress(new byte[] {127,0,0,1}));
            socket.setSoTimeout(5000);
            port = socket.getLocalPort();
        } catch (UnknownHostException e) { // impossible
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException initializing server", e);
        }

    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        isRunning = false;
        thread.interrupt();
        try {
            thread.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Looper.prepare();
        isRunning = true;
        while (isRunning) {
            try {
                Socket client = socket.accept();
                if (client == null) {
                    continue;
                }
                Log.d(LOG_TAG, "client connected");

                StreamToMediaPlayerTask task = new StreamToMediaPlayerTask(client);
                if (task.processRequest()) {
                    task.execute();
                }

            } catch (SocketTimeoutException e) {
                // Do nothing
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to client", e);
            }
        }
        Log.d(LOG_TAG, "Proxy interrupted. Shutting down.");
    }

    /**
     * Async task class that streams data to a media player.
     */
    private static class StreamToMediaPlayerTask extends AsyncTask<String, Void, Integer> {

        String localPath;
        Socket client;
        int cbSkip;

        public StreamToMediaPlayerTask(Socket client) {
            this.client = client;
        }

        private static String readTextStreamAvailable(InputStream inputStream) throws IOException {
            byte[] buffer = new byte[4096];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096);

            // Do the first byte via a blocking read
            outputStream.write(inputStream.read());

            // Slurp the rest
            int available = inputStream.available();
            while (available > 0) {
                int cbToRead = Math.min(buffer.length, available);
                int cbRead = inputStream.read(buffer, 0, cbToRead);
                if (cbRead <= 0) {
                    throw new IOException("Unexpected end of stream");
                }
                outputStream.write(buffer, 0, cbRead);
                available -= cbRead;
            }
            return new String(outputStream.toByteArray());
        }

        public boolean processRequest() {
            // Read HTTP headers
            String headers;
            try {
                headers = readTextStreamAvailable(client.getInputStream());
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error reading HTTP request header from stream:", e);
                return false;
            }

            // Get the important bits from the headers
            String[] headerLines = headers.split("\n");
            String urlLine = headerLines[0];
            if (!urlLine.startsWith("GET ")) {
                Log.e(LOG_TAG, "Only GET is supported");
                return false;
            }
            urlLine = urlLine.substring(4);
            int charPos = urlLine.indexOf(' ');
            if (charPos != -1) {
                urlLine = urlLine.substring(1, charPos);
            }
            localPath = urlLine;

            // See if there's a "Range:" header
            for (int i=0 ; i<headerLines.length ; i++) {
                String headerLine = headerLines[i];
                if (headerLine.startsWith("Range: bytes=")) {
                    headerLine = headerLine.substring(13);
                    charPos = headerLine.indexOf('-');
                    if (charPos>0) {
                        headerLine = headerLine.substring(0,charPos);
                    }
                    cbSkip = Integer.parseInt(headerLine);
                }
            }
            return true;
        }

        @Override
        protected Integer doInBackground(String... params) {

            long fileSize = 32 * 1024; // FIXME which file size here?
            String contentType = "video/mp4"; // FIXME right content type?

            // Create HTTP header
            String headers = "HTTP/1.0 200 OK\r\n";
            headers += "Content-Type: " + contentType + "\r\n";
            headers += "Content-Length: " + fileSize  + "\r\n";
            headers += "Connection: close\r\n";
            headers += "\r\n";

            // Begin with HTTP header
            int fc = 0;
            long cbToSend = fileSize - cbSkip;
            OutputStream output = null;
            byte[] buff = new byte[64 * 1024];
            try {
                output = new BufferedOutputStream(client.getOutputStream(), 32*1024);
                output.write(headers.getBytes());

                // Loop as long as there's stuff to send
                while (isRunning && cbToSend>0 && !client.isClosed()) {

                    // See if there's more to send
                    File file = new File(localPath);
                    fc++;
                    int cbSentThisBatch = 0;
                    if (file.exists()) {
                        FileInputStream input = new FileInputStream(file);
                        input.skip(cbSkip);
                        int cbToSendThisBatch = input.available();
                        while (cbToSendThisBatch > 0) {
                            int cbToRead = Math.min(cbToSendThisBatch, buff.length);
                            int cbRead = input.read(buff, 0, cbToRead);
                            if (cbRead == -1) {
                                break;
                            }
                            cbToSendThisBatch -= cbRead;
                            cbToSend -= cbRead;
                            output.write(buff, 0, cbRead);
                            output.flush();
                            cbSkip += cbRead;
                            cbSentThisBatch += cbRead;
                        }
                        input.close();
                    }

                    // If we did nothing this batch, block for a second
                    if (cbSentThisBatch == 0) {
                        Log.d(LOG_TAG, "Blocking until more data appears");
                        Thread.sleep(1000);
                    }
                }
            }
            catch (SocketException socketException) {
                Log.e(LOG_TAG, "SocketException() thrown, proxy client has probably closed. This can exit harmlessly");
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Exception thrown from streaming task:");
                Log.e(LOG_TAG, e.getClass().getName() + " : " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            // Cleanup
            try {
                if (output != null) {
                    output.close();
                }
                client.close();
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "IOException while cleaning up streaming task:");
                Log.e(LOG_TAG, e.getClass().getName() + " : " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            return 1;
        }
    }
}