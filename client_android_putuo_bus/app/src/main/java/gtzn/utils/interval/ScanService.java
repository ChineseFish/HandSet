package gtzn.utils.interval;

import java.io.IOException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;

import gtzn.utils.log.LogUtils;

public class ScanService extends Service {

    private static String TAG = ScanService.class.getSimpleName();

    public final static int INTERVAL = 10000;

     /**
     * if throw exception, serialport initialize fail.
     *
     * @throws SecurityException
     * @throws IOException
     */
    public ScanService() throws SecurityException {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      
        //
        LogUtils.d("ScanService", "run begin");

        // fetch alarm manager
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // fetch broadcast instance
        Intent i = new Intent(this, Remote.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        //
        long triggerAtTime = SystemClock.elapsedRealtime() + INTERVAL;
        //
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        String CHANNEL_ID = "my_channel_01";
        //
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("My notification").setContentText("Hello World!");

        //
        startForeground(-1, mBuilder.build());

        //
        stopSelf();

        //
        return super.onStartCommand(intent, flags, startId);
    }
}
