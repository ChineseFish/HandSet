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

package com.tongda.shop;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.tongda.base.Constants;
import com.tongda.base.Transfer;
import com.tongda.base.Tts;
import com.tongda.base.Utils;
import com.tongda.base.log.LogUtils;
import com.tongda.debug.DragFloatActionButton;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    //
    private Tts tts = null;

    //
    private static WebView webView;

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        setContentView(R.layout.activity_main);

        /**
         * Debug button
         */
        DragFloatActionButton mBtn = findViewById(R.id.ziubao_float_button);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Transfer.startActivity(MainActivity.this, "ziubao_debug/main", new Intent());
            }
        });

        //
        if(!BuildConfig.DEBUG)
        {
            mBtn.setVisibility(View.INVISIBLE);
        }

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
         *
         */
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
        webView.loadUrl(Constants.Home_Page);

        // add js interface
        webView.addJavascriptInterface(this, "zsgtzn");

        /**
         * 获取权限
         */
        initPermission();

        /**
         * 检测版本更新
         */
        Utils.checkApkUpdate(this);
    }

    public static void loadUrl(String url)
    {
        webView.loadUrl(url);
    }

    @Override
    protected void onResume() {
        // when app at frontground, keep screen on
        super.onResume();
    }

    /**
     * 手动申请权限（特定的权限只能在app内部申请）
     */
    private void initPermission() {
        String[] perms =
                {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.REQUEST_INSTALL_PACKAGES
                };

        //
        if (!EasyPermissions.hasPermissions(this, perms)) {
            // 第二个参数是被拒绝后再次申请该权限的解释
            // 第三个参数是请求码
            // 第四个参数是要申请的权限
            EasyPermissions.requestPermissions(this, "必要的权限", 0, perms);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.removeAllViews();

            //
            try {
                webView.destroy();
            } catch (Throwable t) {

            }

            //
            webView = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 把申请权限的回调交由EasyPermissions处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i("app MainActivity", "获取成功的权限" + perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e("app MainActivit", "获取失败的权限" + perms);
    }

    /**
     * log init
     */
    private void initLog() {
        LogUtils.setLogDir(this.getExternalFilesDir(null) + File.separator + "app_log");
            LogUtils.setLogLevel(LogUtils.LogLevel.DEBUG);
    }

    /**
     * speech imediate
     * @param msg
     */
    @JavascriptInterface
    public void speechGOImmediate(String msg) {
        tts.textToSpeech(getApplicationContext(), msg);
    }
}
