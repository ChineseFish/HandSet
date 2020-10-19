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

package com.tongda.shipgrocery;

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
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

import androidx.annotation.RequiresApi;

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
    private Tts tts = null;

    //
    private WebView webView;

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // when app at frontground, keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Specifies whether an Activity should be shown on top of the lock screen
        // whenever the lockscreen is up and the activity is resumed. Normally an
        // activity will be transitioned to the stopped state if it is started while the
        // lockscreen is up, but with this flag set the activity will remain in the
        // resumed state visible on-top of the lock screen.
        this.setShowWhenLocked(true);

        //
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
        webView.loadUrl("https://yds.ziubao.com/xmb/");

        //
        initLog();

        // disable battery optimizations
        if (!isIgnoringBatteryOptimizations()) {
            //
            requestIgnoreBatteryOptimizations();
        }

        //
        tts = new Tts();

        // add js interface
        webView.addJavascriptInterface(this, "zsgtzn");
    }

    @Override
    protected void onResume() {
        // when app at frontground, keep screen on
        super.onResume();
    }

    /************************************************
     * log init
     ************************************************/
    private void initLog() {
        LogUtils.setLogDir(this.getExternalFilesDir(null)+ File.separator + "app_log");
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
}
