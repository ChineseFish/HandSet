package com.tongda.printer;

import android.content.SharedPreferences;
import android.util.Log;

class Db {
    private static String addressKey = "address";
    private static String addressTypeKey = "addressType";

    // 存数据
    static public void writeAddress(SharedPreferences sp, String data) {
        //
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(addressKey, data);
        editor.commit();

        //
        Log.i("Db", "writeAddress, key: " + addressKey + ", value: " + data);
    }

    // 读数据
    static public String getAddress(SharedPreferences sp) {
        //
        String address = sp.getString(addressKey, "");

        Log.i("Db", "getAddress, key: " + addressKey + ", value: " + address);

        return address;
    }

    // 存数据
    static public void writeAddressType(SharedPreferences sp, String data) {
        //
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(addressTypeKey, data);
        editor.commit();

        //
        Log.i("Db", "writeAddressType, key: " + addressTypeKey + ", value: " + data);
    }

    // 读数据
    static public String getAddressType(SharedPreferences sp) {
        //
        String addressType = sp.getString(addressTypeKey, "");

        Log.i("Db", "getAddressType, key: " + addressTypeKey + ", value: " + addressType);

        return addressType;
    }
}