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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import org.apache.cordova.CordovaActivity;

import gtzn.cordova.interval.*;

public class MainActivity extends CordovaActivity
{
    public MainActivity() {
        mainActivity = this;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }
    public static SharedPreferences getSharedPreferences() {
        return mainActivity.mSp;
    }
    private static MainActivity mainActivity;

    private static String identifier = "";
    private ScanThread scanThread = null;

    public SharedPreferences mSp;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // enable Cordova apps to be started in the background
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        //
        mSp = getSharedPreferences("gtzn", MODE_PRIVATE);

        //
        if(appView == null)
            init();

        WebView webView = (WebView)appView.getView();
        WebSettings settings = webView.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        // add js interface
        webView.addJavascriptInterface(this, "zsgtzn");

        //
        if(!isIgnoringBatteryOptimizations())
        {
            //
            requestIgnoreBatteryOptimizations();
        }

        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);
    }

    @SuppressLint("HandlerLeak")
    private Handler scanHandler = new Handler()
    {
        public void handleMessage(Message msg) {
            Log.d("scanHandler", "begin to work");

            Remote.fetchPayInfo(MainActivity.identifier);
        }
    };

    @JavascriptInterface
    public void speechGO(String msg) {
        //
        MainActivity.identifier = msg;

        //
        if(scanThread != null)
        {
            Log.d("setIndentifier", "scanThread has begun");

            return;
        }

        //
        try
        {
            scanThread = new ScanThread(scanHandler);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "支付接口调用失败，线程吊起失败", Toast.LENGTH_SHORT).show();

            return;
        }

        //
        scanThread.start();
    }

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
