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

package com.tongda.putuoshanlvyoubashi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

import gtzn.utils.interval.Interval;
import gtzn.utils.interval.Tts;

import gtzn.utils.log.LogUtils;

public class MainActivity extends Activity {
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
    private Interval interval = null;
    private Tts tts = null;

    //
    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        mSp = getSharedPreferences("gtzn", MODE_PRIVATE);

        //
        webView = findViewById(R.id.webView);
        // 开启 localStorage
        webView.getSettings().setDomStorageEnabled(true);
        // 设置支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 启动缓存
        webView.getSettings().setAppCacheEnabled(true);
        // 设置缓存模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        //
        webView.getSettings().setLoadWithOverviewMode(true);
        // 开启视口模式
        webView.getSettings().setUseWideViewPort(true);
        // 使用自定义的WebViewClient
        webView.setWebViewClient(new WebViewClient()
        {
            // 覆盖shouldOverrideUrlLoading 方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {

            }

        });
        webView.loadUrl("https://mg.zhoulvkeche.com/login.aspx");

        //
        initLog();

        // disable battery optimizations
        if (!isIgnoringBatteryOptimizations()) {
            //
            requestIgnoreBatteryOptimizations();
        }

        //
        tts = new Tts();
        interval = new Interval();

        // add js interface
        webView.addJavascriptInterface(this, "zsgtzn");
    }

    /************************************************
     * log init
     ************************************************/
    private void initLog() {
        LogUtils.setLogDir(this.getExternalFilesDir(null)+ File.separator + "putuoshanlvyoubashi_log");
            LogUtils.setLogLevel(LogUtils.LogLevel.DEBUG);
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
