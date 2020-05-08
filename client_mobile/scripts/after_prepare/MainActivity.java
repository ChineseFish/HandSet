/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package __PACKAGE_NAME__;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.LOG;

import java.io.File;

import gtzn.cordova.interval.Interval;
import gtzn.cordova.interval.Tts;

import gtzn.utils.log.LogUtils;

public class MainActivity extends CordovaActivity {
    //
    private static MainActivity mainActivity;

    public MainActivity() {
        mainActivity = this;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    //
    public SharedPreferences mSp;

    public static SharedPreferences getSharedPreferences() {
        return mainActivity.mSp;
    }

    //
    private static String identifier = "";

    //
    private Interval interval = null;
    private Tts tts = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // enable Cordova apps to be started in the background
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        //
        mSp = getSharedPreferences("gtzn", MODE_PRIVATE);

        //
        if (appView == null)
            init();

        WebView webView = (WebView) appView.getView();
        WebSettings settings = webView.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        // check privilege
        if (!isIgnoringBatteryOptimizations()) {
            //
            requestIgnoreBatteryOptimizations();
        }

        //
        checkStoragePermission();

        // init log
        initLog();

        // add js interface
        webView.addJavascriptInterface(this, "zsgtzn");

        //
        tts = new Tts();
        interval = new Interval();

        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);
    }

    /************************************************
     * log init
     ************************************************/
    private void initLog() {
        LogUtils.setLogDir(Environment.getExternalStorageDirectory()+ File.separator + "putuoshanlvyoubashi_log");
            LogUtils.setLogLevel(LogUtils.LogLevel.DEBUG);

//            LogUtils.setLogLevel(LogUtils.LogLevel.WARN);
    }
    
    /************************************************
     * speech imediate
     ************************************************/
    @JavascriptInterface
    public void speechGOImmediate(String msg) {
        tts.textToSpeech(msg);
    }

    /************************************************
     * speech interval
     ************************************************/
    @JavascriptInterface
    public void speechGODestroy() {
        interval.stop();
    }

    @JavascriptInterface
    public void speechGO(String identifier, String index) {
        interval.start(identifier, index);
    }

    /************************************************
     * check storage privilege
     ************************************************/
    public void checkStoragePermission() {
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                isGranted = false;
            }
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            Log.i("cbs","isGranted == "+isGranted);
            if (!isGranted) {
                this.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                                .ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        102);
            }
        }

    }

    /************************************************
     * check battery privilege
     ************************************************/
    @TargetApi(Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }

    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到指定应用的首页
     */
    private void showActivity(String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        startActivity(intent);
    }

    /**
     * 跳转到指定应用的指定页面
     */
    private void showActivity(String packageName, String activityDir) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityDir));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static boolean isOPPO() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("oppo");
    }

    private void goOPPOSetting() {
        try {
            showActivity("com.coloros.phonemanager");
        } catch (Exception e1) {
            try {
                showActivity("com.oppo.safe");
            } catch (Exception e2) {
                try {
                    showActivity("com.coloros.oppoguardelf");
                } catch (Exception e3) {
                    showActivity("com.coloros.safecenter");
                }
            }
        }
    }
}
