package com.tongda.shop;

import android.app.Application;
import android.content.Context;
import com.tongda.base.Injector;

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        MyApplication.context = getApplicationContext();

        //
        Injector.inject();
    }

    public static Context getContext() {
        return MyApplication.context;
    }
}
