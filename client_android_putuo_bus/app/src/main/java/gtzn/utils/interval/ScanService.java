package gtzn.utils.interval;

import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
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

import com.tongda.putuoshanlvyoubashi.MainActivity;
import com.tongda.putuoshanlvyoubashi.MyApplication;
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
        LogUtils.setCacheQueueSize(0);
        
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

    /**
     * 将本应用置顶到最前端 当本应用位于后台时，则将它切换到最前端
     * @param context
     */
    public static void setTopApp(Context context) {
        if (!isRunningForeground(context)) {
            // 获取ActivityManager
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

            // 获得当前运行的task(任务)
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {

                // 找到本应用的 task，并将它切换到前台
                if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
                    activityManager.moveTaskToFront(taskInfo.id, 0);
                    break;
                }
            }
        }
    }

    /**
     * 判断本应用是否已经位于最前端
     * @param context
     * @return 本应用已经位于最前端时，返回 true；否则返回 false
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        
        // 枚举进程
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //
        setIntervalAlarm();

        //
        setTopApp(this);

        //
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
