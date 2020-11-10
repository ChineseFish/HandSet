package com.tongda.base;

import android.content.Context;

import java.io.File;

public class Constants {
    //
    public static String Home_Page;
    public static String ApkDownloadUrl;
    public static String CheckVersionUrl;
    public static String server;
    public static String cookie;

    //
    public static String apkSavePath = null;
    public static String apkFilePath = null;

    //
    public static void setCookie(String _cookie)
    {
        cookie = _cookie;
    }

    //
    public static void init(Context context)
    {
        if(apkSavePath != null && apkFilePath != null)
        {
            return;
        }

        //
        apkSavePath = context.getExternalFilesDir(null) + File.separator + "logistic";
        apkFilePath = apkSavePath +  File.separator + "logistic.apk";

        //
        if(BuildConfig.DEBUG)
        {
            Home_Page = "https://shop.ziubao.com/border/BOrderJsp.do";
            server = "https://shop.ziubao.com";
            ApkDownloadUrl = "http://pay.ziubao.com:8088/android/shop.apk";
            CheckVersionUrl = "http://pay.ziubao.com:8088/getAPKVersion?fileName=shop.apk";
        }
        else
        {
            Home_Page = "https://shop.ziubao.com/border/BOrderJsp.do";
            server = "https://shop.ziubao.com";
            ApkDownloadUrl = "http://pay.ziubao.com:8088/android/shop.apk";
            CheckVersionUrl = "http://pay.ziubao.com:8088/getAPKVersion?fileName=shop.apk";
        }
    }
}
