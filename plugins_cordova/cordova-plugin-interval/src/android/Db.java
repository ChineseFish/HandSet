package gtzn.cordova.interval;

import android.content.SharedPreferences;

import __PACKAGE_NAME__.MainActivity;

import gtzn.utils.log.LogUtils;

class Db {
    // 存数据
    static public void writeBusIdentifierIndex(String identifier, String index) {
        //
        SharedPreferences.Editor editor = MainActivity.getSharedPreferences().edit();
        editor.putString(identifier, index);
        editor.commit();

        //
        LogUtils.d("Db", "writeBusIdentifierIndex, key: " + identifier + ", value: " + index);
    }

    // 读数据
    static public String getBusIdentifierIndex(String identifier) {
        //
        String index = MainActivity.getSharedPreferences().getString(identifier, "0");

        LogUtils.d("Db", "getBusIdentifierIndex, key: " + identifier + ", value: " + index);

        return index;
    }
}