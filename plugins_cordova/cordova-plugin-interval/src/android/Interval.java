package gtzn.cordova.interval;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.locks;

class Interval {
    private ScanThread scanThread = null;
    private String identifier = null;
    private Remote remote = null;
    private Lock remoteLock = new Lock();

    public Interval() {

    }

    @SuppressLint("HandlerLeak")
    private Handler scanHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d("Interval", "scanHandler begin to work");

            //
            remoteLock.lock();

            remote.fetchPayInfo(identifier);

            remoteLock.lock().unlock();
        }
    };

    public boolean start(String identifier, String index) {
        Log.d("Interval", "start, begin");

        //
        remoteLock.lock();

        if (remote != null) {
            remote.stop();
        }
        //
        remote = new Remote();

        remoteLock.unlock();

        // change idendifier
        this.identifier = identifier;

        // init index of idendifier
        // notice if repeated start with identical identifier,
        // will result in a competition situation, occur repeat speech,
        Db.writeBusIdentifierIndex(identifier, index);

        // check scanThread
        if (scanThread != null) {
            Log.d("Interval", "start, thread has begun");

            //
            return true;
        }

        //
        try {
            scanThread = new ScanThread(scanHandler);
        } catch (Exception e) {
            return false;
        }
        scanThread.start();

        //
        return true;
    }

    public void stop() {
        scanThread.interrupt();
    }
}