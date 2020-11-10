package com.tongda.debug;

import android.content.SharedPreferences;
import android.util.Log;

class Db {
    private static String jumpUrlKey = "jumpUrl";

    // 存数据
    static public void writeJumpUrl(SharedPreferences sp, String url) {
        //
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(jumpUrlKey, url);
        editor.commit();

        //
        Log.i("Db", "writeJumpUrl, key: " + jumpUrlKey + ", value: " + url);
    }

    // 读数据
    static public String getJumpUrl(SharedPreferences sp) {
        //
        String jumpUrl = sp.getString(jumpUrlKey, "");

        Log.i("Db", "getJumpUrl, key: " + jumpUrlKey + ", value: " + jumpUrl);

        return jumpUrl;
    }
}