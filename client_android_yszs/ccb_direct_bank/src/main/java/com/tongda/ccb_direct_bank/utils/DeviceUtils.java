package com.tongda.ccb_direct_bank.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.lidroid.xutils.util.LogUtils;

/**
 * Created by wutw on 2018/1/4 0004.
 */
public class DeviceUtils {
    /**
     * @return 当前应用版本号
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionCode = 1;
            if (LogUtils.allowD) {
                e.printStackTrace();
            }
        }
        return versionCode;
    }

    /**
     * @return 当前应用版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "";
            if (LogUtils.allowD) {
                e.printStackTrace();
            }
        }
        return versionName;
    }

    //手机设备id
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String deviceId = "";

        try {
            deviceId = !TextUtils.isEmpty(tm.getDeviceId()) ? tm.getDeviceId() : "";
        } catch(Exception e)
        {

        }

        //
        return deviceId;
    }

    public static void getSignature(Context con){
        try {
            String pkgname = con.getPackageName();
            PackageManager manager = con.getPackageManager();
            /** 通过包管理器获得指定包名包含签名的包信息 **/
            PackageInfo packageInfo = manager.getPackageInfo(pkgname, PackageManager.GET_SIGNATURES);
            /******* 通过返回的包信息获得签名数组 *******/
            Signature[] signatures = packageInfo.signatures;
            /************** 得到应用签名 **************/
            String signature = signatures[0].toCharsString();

            /************** 请将signature的值即应用签名 反馈给我 谢谢 **************/
            LogUtils.i("get signature:"+ signature);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
