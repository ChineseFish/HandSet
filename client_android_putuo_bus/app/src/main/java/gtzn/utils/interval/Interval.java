package gtzn.utils.interval;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tongda.putuoshanlvyoubashi.MainActivity;

import gtzn.utils.log.LogUtils;

public class Interval {
    private boolean ifScanServiceStart = false;
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
        if (ifScanServiceStart == true) {
            LogUtils.d("Interval", "start, scanService has begun");

            //
            return true;
        }

        // begin scan service
        Intent intent = new Intent(MainActivity.getMainActivity(), ScanService.class);
        MainActivity.getMainActivity().startService(intent);

        //
        LogUtils.d("Interval", "start, scanService begun");

        //
        return true;
    }

    public void stop() {
        //
        if(false == ifScanServiceStart)
        {
            return;
        }

        // stop alarm manager
        AlarmManager manager = (AlarmManager) MainActivity.getMainActivity().getSystemService(Context.ALARM_SERVICE);
        // fetch broadcast instance
        Intent intent = new Intent(MainActivity.getMainActivity(), Remote.class);
        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.getMainActivity(), 0, intent, 0);
        //
        manager.cancel(pi);

        //
        ifScanServiceStart = false;

        //
        LogUtils.d("Interval", "stop, scanService end");
    }
}