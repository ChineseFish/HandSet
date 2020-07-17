package gtzn.utils.interval;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tongda.putuoshanlvyoubashi.MainActivity;

import gtzn.utils.log.LogUtils;

public class Interval {
    private boolean ifSpeechBroadCastRunning = false;
    public static String identifier = null;

    public Interval() {

    }

    public boolean start(String identifier, String index) {
        LogUtils.d("Interval", "start, begin");

        // change idendifier
        this.identifier = identifier;

        // init index of idendifier
        // notice if repeated start with identical identifier,
        // will result in a competition situation, occur repeat speech,
        Db.writeBusIdentifierIndex(identifier, index);

        // check scanService
        if (ifSpeechBroadCastRunning == true) {
            LogUtils.d("Interval", "start, scanService has begun");

            //
            return true;
        }

        //
        stop();

        //
        ifSpeechBroadCastRunning = true;

        // begin alarm
        Intent intent = new Intent(MainActivity.getMainActivity(), ScanService.class);
        // If this service is not already running,
        // it will be instantiated and started (creating a process for it if needed);
        // if it is running then it remains running.
        MainActivity.getMainActivity().startService(intent);

        //
        LogUtils.d("Interval", "start, scanService begun");

        //
        return true;
    }

    public void stop() {
        // fetch alarm manager
        AlarmManager manager = (AlarmManager) MainActivity.getMainActivity().getSystemService(Context.ALARM_SERVICE);
        // fetch broadcast receiver
        Intent intent = new Intent(MainActivity.getMainActivity(), Remote.class);
        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.getMainActivity(), 0, intent, 0);
        // stop broadcast receiver
        manager.cancel(pi);

        //
        ifSpeechBroadCastRunning = false;

        //
        LogUtils.d("Interval", "stop, Remote end");
    }
}