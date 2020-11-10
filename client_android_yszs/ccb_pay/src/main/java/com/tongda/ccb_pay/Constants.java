package com.tongda.ccb_pay;

import android.content.Context;
import android.widget.Toast;

import com.tongda.base.BuildConfig;
import com.tongda.base.Utils;

import org.json.JSONObject;

public class Constants {

    private static JSONObject config = null;

    public static void init(Context context)
    {

        if(config == null)
        {
            try
            {
                config = Utils.getJson(context, "ccbConfig.json");
            }
            catch(Exception e)
            {
                Toast.makeText(context, "建行支付参数初始化错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getConfig(Context context, String type, String key) {
        try
        {
            JSONObject realConfig;

            if(BuildConfig.DEBUG)
            {
                realConfig = config.getJSONObject("debug");
            }
            else
            {
                realConfig = config.getJSONObject("prod");
            }

            //
            return realConfig.getJSONObject(type).getString(key);
        }
        catch(Exception e)
        {
            Toast.makeText(context, "建行支付获取参数错误, type: " + type + ", key: " + key, Toast.LENGTH_SHORT).show();

            //
            return "";
        }
    }
}