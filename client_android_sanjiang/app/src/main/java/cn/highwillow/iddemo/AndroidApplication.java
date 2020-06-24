package cn.highwillow.iddemo;

import android.app.Application;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Changhe on 2018-01-12.
 */

public class AndroidApplication extends Application {
    private static AndroidApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initErrorHandler();
    }


    private void initErrorHandler() {
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(this);
    }


    public static AndroidApplication getInstance() {
        return instance;
    }
}
