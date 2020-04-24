package gtzn.cordova.interval;

import java.io.IOException;

import android.os.Handler;
import android.os.Message;


public class ScanThread extends Thread {

    private static String TAG = ScanThread.class.getSimpleName();

    private Handler handler;

    public final static int INTERVAL = 2000;

    /**
     * if throw exception, serialport initialize fail.
     *
     * @throws SecurityException
     * @throws IOException
     */
    public ScanThread(Handler handler) throws SecurityException, IOException {
        this.handler = handler;
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {

                Message msg = new Message();

                handler.sendMessage(msg);

                Thread.sleep(INTERVAL);    
            }
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }
}