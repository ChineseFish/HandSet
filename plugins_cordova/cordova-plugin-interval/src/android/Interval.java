package gtzn.cordova.interval;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gtzn.utils.log.LogUtils;

public class Interval {
    private ScanThread scanThread = null;
    private String identifier = null;
    private Remote remote = null;
    private Lock remoteLock = new ReentrantLock();

    public Interval() {

    }

    @SuppressLint("HandlerLeak")
    private Handler scanHandler = new Handler() {
        public void handleMessage(Message msg) {
            LogUtils.d("Interval", "scanHandler begin to work");

            //
            try
            {
                remoteLock.lock();

                remote.fetchPayInfo(identifier);
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                LogUtils.d("Interval handleMessage", "释放了锁");

                remoteLock.unlock();
            }
        }
    };

    public boolean start(String identifier, String index) {
        LogUtils.d("Interval", "start, begin");

        //
        if(remote != null)
        {
            try
            {
                //
                remoteLock.lock();

                remote.stopFetchPayInfo();

                remote = new Remote();
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                LogUtils.d("Interval handleMessage", "释放了锁");

                remoteLock.unlock();
            }
        }
        else
        {
            remote = new Remote();
        }

        // change idendifier
        this.identifier = identifier;

        // init index of idendifier
        // notice if repeated start with identical identifier,
        // will result in a competition situation, occur repeat speech,
        Db.writeBusIdentifierIndex(identifier, index);

        // check scanThread
        if (scanThread != null) {
            LogUtils.d("Interval", "start, thread has begun");

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
        //
        if (remote != null) {
            try
            {
                //
                remoteLock.lock();

                //
                remote.stopFetchPayInfo();

                //
                remote = null;
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                LogUtils.d("Interval handleMessage", "释放了锁");

                remoteLock.unlock();
            }
        }
        
        //
        if(null != scanThread)
        {
            scanThread.interrupt();

            scanThread = null;
        }
    }
}