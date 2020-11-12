package com.tongda.base;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;

public abstract class Service {
    // app
    public void app_jumpToUrl(final Context context, String url) {
        Toast.makeText(context, "请实现这个方法", Toast.LENGTH_SHORT).show();
    }


    // printer
    public void printer_printBill(final Context context, String content) {
        Toast.makeText(context, "请实现这个方法", Toast.LENGTH_SHORT).show();
    }

    public void printer_init(final Context context)
    {
        Toast.makeText(context, "请实现这个方法", Toast.LENGTH_SHORT).show();
    }
}
