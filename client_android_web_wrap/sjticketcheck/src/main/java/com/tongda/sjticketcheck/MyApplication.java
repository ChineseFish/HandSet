package com.tongda.sjticketcheck;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //
        initErrorHandler();
    }

    private void initErrorHandler() {
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(this);
    }
}
