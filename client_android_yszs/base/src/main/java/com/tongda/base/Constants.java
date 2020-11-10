package com.tongda.base;

import android.content.Context;

import java.io.File;

public class Constants {
    //
    public static String Home_Page;
    public static String ApkDownloadUrl;
    public static String CheckVersionUrl;

    //
    public static String apkSavePath = null;
    public static String apkFilePath = null;

    //
    public static void init(Context context)
    {
        if(apkSavePath != null && apkFilePath != null)
        {
            return;
        }

        //
        apkSavePath = context.getExternalFilesDir(null)+ File.separator + "yszs";
        apkFilePath = apkSavePath +  File.separator + "yszs.apk";

        //
        if(BuildConfig.DEBUG)
        {
            Home_Page = "http://192.168.11.187:3000/ziubao";
            ApkDownloadUrl = "http://192.168.11.218:3000/apk/yszs.apk";
            CheckVersionUrl = "http://192.168.11.218:3000/apk/getAPKVersion?fileName=yszs.apk";
        }
        else
        {
            Home_Page = "https://zyb.ziubao.com";
            ApkDownloadUrl = "http://pay.ziubao.com:8088/android/yszs.apk";
            CheckVersionUrl = "http://pay.ziubao.com:8088/getAPKVersion?fileName=yszs.apk";
        }
    }
}
