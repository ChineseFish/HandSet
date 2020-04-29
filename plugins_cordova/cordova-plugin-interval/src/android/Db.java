package gtzn.cordova.interval;

import android.content.SharedPreferences;
import android.util.Log;

import __PACKAGE_NAME__.MainActivity;

class Db {
  // 存数据
  static public void writeBusIdentifierIndex(String identifier, String index) {
      //
      SharedPreferences.Editor editor = MainActivity.getSharedPreferences().edit();
      editor.putString(identifier, index);
      editor.commit();

      //
      Log.d("Db", "writeBusIdentifierIndex, key: " + identifier + ", value: " + index);
  }

  // 读数据
  static public String getBusIdentifierIndex(String identifier) {
      //
      String index = MainActivity.getSharedPreferences().getString(identifier, "0");

      Log.d("Db", "getBusIdentifierIndex, key: " + identifier + ", value: " + index);

      return index;
  }
}