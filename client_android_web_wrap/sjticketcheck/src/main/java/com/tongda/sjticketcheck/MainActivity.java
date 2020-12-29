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

package com.tongda.sjticketcheck;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.util.List;

import androidx.annotation.RequiresApi;

import com.tongda.base.log.LogUtils;
import com.tongda.djidcard.DjMainActivity;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends DjMainActivity implements EasyPermissions.PermissionCallbacks {
    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        setContentView(R.layout.activity_main);

        //
        webView = findViewById(R.id.webView);
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        // 开启 localStorage
        webView.getSettings().setDomStorageEnabled(true);
        // 设置支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
//        // 启动缓存
//        webView.getSettings().setAppCacheEnabled(true);
//        // 设置缓存模式
//        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        //
        webView.getSettings().setLoadWithOverviewMode(true);
        // 开启视口模式
        webView.getSettings().setUseWideViewPort(true);
        // 使用自定义的WebViewClient
        // WebViewClient is used fo rendering HTML
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                if (url.startsWith("tel:")) {
                    //
                    Intent intent = new Intent(Intent.ACTION_VIEW,  Uri.parse(url));
                    startActivity(intent);

                    //
                    return true;
                }

                //
                view.loadUrl(url);

                //
                return true;
            }
        });
        webView.loadUrl("https://sjbp.ziubao.com/");

        //
        initLog();

        //
        initPermission();
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
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                };

        //
        if (!EasyPermissions.hasPermissions(this, perms)) {
            // 第二个参数是被拒绝后再次申请该权限的解释
            // 第三个参数是请求码
            // 第四个参数是要申请的权限
            EasyPermissions.requestPermissions(this, "必要的权限", 0, perms);
        }
    }

    /**
     * log init
     */
    private void initLog() {
        LogUtils.setLogDir(this.getExternalFilesDir(null)+ File.separator + "app_log");
            LogUtils.setLogLevel(LogUtils.LogLevel.DEBUG);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.e("app MainActivity", "获取成功的权限" + perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e("app MainActivit", "获取失败的权限" + perms);
    }
}
