package gtzn.utils.interval;

import java.io.IOException;

import android.os.Handler;
import android.os.Message;

import gtzn.utils.log.LogUtils;

public class ScanThread extends Thread {

    private static String TAG = ScanThread.class.getSimpleName();

    private Handler handler;

    public final static int INTERVAL = 10000;

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
            // isInterrupted() will throw an exception when thread call interrupted()
            // and interrupted mark is set to true
            while (!isInterrupted()) {
                long startTime = System.currentTimeMillis();

                //
                LogUtils.d("ScanThread", "run begin");

                //
                Message msg = new Message();

                handler.sendMessage(msg);

                Thread.sleep(INTERVAL);

                //
                long endTime = System.currentTimeMillis();
                LogUtils.d("ScanThread", "run end, elapsed time：" + (endTime - startTime) + "ms");
            }

            //
            LogUtils.d("ScanThread", "--------------------------\nthread is over, can not run to here!!\n--------------------------");
        } catch (Exception e) {
            //
            LogUtils.d("ScanThread", "--------------------------\nthread is over, success, " + e.toString() + "\n--------------------------");

            //
            e.printStackTrace();
        }
    }
}
