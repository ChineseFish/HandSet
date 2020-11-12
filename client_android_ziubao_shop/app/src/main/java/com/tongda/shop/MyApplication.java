package com.tongda.shop;


import android.app.Application;

import com.tongda.base.Constants;
import com.tongda.base.Injector;
import com.tongda.base.Service;
import com.tongda.base.Transfer;
import com.tongda.base.Tts;

import com.tongda.base.log.LogUtils;

import java.io.File;

public class MyApplication extends Application {
    //
    public static Tts tts = null;

    @Override
    public void onCreate() {
        super.onCreate();

        //
        Injector.inject();

        /**
         *
         */
        Constants.init(this);

        /**
         *
         */
        initLog();

        /**
         *
         */
        tts = Tts.getInstance();


        /**
         * 初始化打印机
         */
        Service printService = Transfer.obtainService("printer");
        printService.printer_init(this);
    }

    /**
     * log init
     */
    private void initLog() {
        LogUtils.setLogDir(this.getExternalFilesDir(null) + File.separator + "app_log");
        LogUtils.setLogLevel(LogUtils.LogLevel.DEBUG);
    }
}
