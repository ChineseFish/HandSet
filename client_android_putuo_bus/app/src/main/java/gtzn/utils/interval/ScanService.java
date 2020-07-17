package gtzn.utils.interval;

import java.io.IOException;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.tongda.putuoshanlvyoubashi.R;

import gtzn.utils.aop.PrintTimeElapseAnnotation;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        //
        startForegroundService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @PrintTimeElapseAnnotation("ScanService setIntervalAlarm")
    public void setIntervalAlarm()
    {
        //
        LogUtils.d("ScanService setIntervalAlarm", "run begin");

        // fetch alarm manager
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // fetch broadcast instance
        Intent i = new Intent(this, Remote.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        // set a timer broadcast
        long triggerAtTime = SystemClock.elapsedRealtime() + INTERVAL;
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setIntervalAlarm();

        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName){
        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);

        //
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        //
        NotificationManager service = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);

        //
        return channelId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startForegroundService() {
        String channelId = createNotificationChannel("putuobus", "speech");

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("普陀山巴士")
                    .setContentText("语音播报进行中")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .build();


        startForeground(101, notification);
    }
}
