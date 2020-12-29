package com.tongda.sjticketcheck;

import android.content.Context;
import android.util.Log;

/**
 * Created by Changhe on 2018-01-12.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = CrashHandler.class.getSimpleName();
    private static CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;


    private CrashHandler() {
    }


    public static CrashHandler getInstance() {
        return INSTANCE;
    }


    public static int debugLine(Exception e) {
        StackTraceElement[] trace = e.getStackTrace();
        if (trace == null || trace.length == 0)
            return -1; //
        return trace[0].getLineNumber();
    }

    public static String debugFunction(Exception e) {
        StackTraceElement[] trace = e.getStackTrace();
        if (trace == null)
            return ""; //
        return trace[0].getMethodName();
    }

    public static String debugFileName(Exception e) {
        StackTraceElement[] trace = e.getStackTrace();
        if (trace == null)
            return ""; //
        return trace[0].getFileName();

    }

    public static String debugFileFunctionLine(Exception e) {
        return debugFileName(e) + ":" + debugFunction(e) + ":" + debugLine(e);
    }
    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        // if (!handleException(ex) && mDefaultHandler != null) {
        // mDefaultHandler.uncaughtException(thread, ex);
        // } else {
        // android.os.Process.killProcess(android.os.Process.myPid());
        // System.exit(10);
        // }

        Log.e("LOTUS_CARD_DRIVER", debugFileFunctionLine(new Exception()) + ":" + throwable.toString());
//        System.out.println("uncaughtException");
//
//
//        new Thread() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                new AlertDialog.Builder(mContext).setTitle("提示").setCancelable(false)
//                        .setMessage("程序崩溃了...").setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        System.exit(0);
//                    }
//                })
//                        .create().show();
//                Looper.loop();
//            }
//        }.start();
    }

}
